package pluto.upik.domain.option.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pluto.upik.domain.guide.data.DTO.KeywordGuideResponse;
import pluto.upik.domain.guide.service.KeywordGuideServiceInterface;
import pluto.upik.domain.option.data.DTO.GenerateOptionsResponse;
import pluto.upik.domain.option.data.DTO.GuideSimpleInfo;
import pluto.upik.domain.option.data.DTO.SimilarGuidesResponse;
import pluto.upik.shared.ai.config.ChatAiService;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.exception.ResourceNotFoundException;
import pluto.upik.shared.translation.service.TranslationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * 선택지 생성 서비스 구현 클래스
 * AI를 활용하여 제목에 맞는 선택지를 생성합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OptionGeneratorServiceImpl implements OptionGeneratorServiceInterface {

    private final ChatAiService chatAiService;
    private final TranslationService translationService;
    private final KeywordGuideServiceInterface keywordGuideService;

    private static final int AI_RESPONSE_TIMEOUT_SECONDS = 30;
    private static final int MAX_SUMMARY_LENGTH = 100;

    /**
     * 제목에 맞는 선택지를 생성합니다.
     *
     * @param title 제목
     * @param count 생성할 선택지 개수
     * @return 생성된 선택지 응답
     */
    @Override
    public GenerateOptionsResponse generateOptions(String title, int count) {
        log.info("선택지 생성 시작 - 제목: {}, 개수: {}", title, count);
        
        try {
            CompletableFuture<GenerateOptionsResponse> future = generateOptionsAsync(title, count);
            GenerateOptionsResponse response = future.get(AI_RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            
            log.info("선택지 생성 완료 - 제목: {}, 생성된 선택지 개수: {}", title, response.getOptions().size());
            return response;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("선택지 생성 중 인터럽트 발생 - 제목: {}, 개수: {}", title, count, e);
            return buildErrorResponse("선택지 생성이 중단되었습니다.");
        } catch (ExecutionException e) {
            log.error("선택지 생성 중 오류 발생 - 제목: {}, 개수: {}", title, count, e);
            return buildErrorResponse("선택지 생성 중 오류가 발생했습니다: " + e.getCause().getMessage());
        } catch (TimeoutException e) {
            log.error("선택지 생성 시간 초과 - 제목: {}, 개수: {}", title, count, e);
            return buildErrorResponse("선택지 생성 시간이 초과되었습니다.");
        }
    }
    
    /**
     * 제목에 맞는 선택지를 비동기적으로 생성합니다.
     *
     * @param title 제목
     * @param count 생성할 선택지 개수
     * @return 생성된 선택지 응답의 CompletableFuture
     */
    @Async("taskExecutor")
    public CompletableFuture<GenerateOptionsResponse> generateOptionsAsync(String title, int count) {
        try {
            // 한국어 제목을 영어로 번역
            String translatedTitle = translationService.translateKoreanToEnglish(title);
            log.debug("번역된 제목: {}", translatedTitle);
            
            // AI에게 선택지 생성 요청 - 개선된 프롬프트
            String prompt = String.format(
                "You are a poll option generator. I need you to generate %d specific, concrete options for a poll titled: \"%s\".\n\n" +
                "IMPORTANT RULES:\n" +
                "1. Each option must be a DIRECT ANSWER to the poll title, not a question.\n" +
                "2. Keep each option SHORT (1-5 words) and SPECIFIC.\n" +
                "3. For travel destinations, provide actual place names (cities, countries, landmarks).\n" +
                "4. DO NOT generate questions - only provide direct answers/options.\n" +
                "5. For example, if the title is 'Favorite Travel Destination', respond with place names like 'Paris', 'Tokyo', 'Grand Canyon', etc.\n\n" +
                "Format your response EXACTLY like this:\n" +
                "1. [option1]\n" +
                "2. [option2]\n" +
                "3. [option3]\n" +
                "... and so on.\n\n" +
                "DO NOT include any explanation or additional text - ONLY the numbered list of options.",
                count, translatedTitle
            );
            
            String aiResponse = chatAiService.askToDeepSeekAI(prompt);
            log.debug("AI 응답: {}", aiResponse);
            
            // AI 응답에서 선택지 추출
            List<String> englishOptions = extractOptionsFromAiResponse(aiResponse, count);
            
            // 영어 선택지를 한국어로 번역
            List<String> koreanOptions = englishOptions.stream()
                .map(option -> translationService.translateEnglishToKorean(option))
                .collect(Collectors.toList());
            
            return CompletableFuture.completedFuture(
                GenerateOptionsResponse.builder()
                    .success(true)
                    .message("선택지가 성공적으로 생성되었습니다.")
                    .options(koreanOptions)
                    .build()
            );
        } catch (Exception e) {
            log.error("선택지 비동기 생성 중 오류 - 제목: {}, 개수: {}", title, count, e);
            throw new BusinessException("선택지 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * AI 응답에서 선택지를 추출합니다.
     *
     * @param aiResponse AI 응답 텍스트
     * @param expectedCount 예상되는 선택지 개수
     * @return 추출된 선택지 목록
     */
    private List<String> extractOptionsFromAiResponse(String aiResponse, int expectedCount) {
        if (aiResponse == null || aiResponse.trim().isEmpty()) {
            log.warn("AI 응답이 비어있습니다.");
            throw new BusinessException("AI가 선택지를 생성하지 못했습니다.");
        }
        
        // 번호 패턴(1., 2. 등)으로 시작하는 줄을 찾아 선택지로 추출
        List<String> options = new ArrayList<>();
        String[] lines = aiResponse.split("\n");
        
        for (String line : lines) {
            String trimmedLine = line.trim();
            // 번호 패턴 확인 (예: "1. ", "2. " 등)
            if (trimmedLine.matches("^\\d+\\..*")) {
                // 번호와 점(.)을 제거하고 앞뒤 공백 제거
                String option = trimmedLine.replaceFirst("^\\d+\\.\\s*", "").trim();
                if (!option.isEmpty() && !option.endsWith("?")) {  // 질문 형태 제외
                    options.add(option);
                }
            }
        }
        
        // 충분한 선택지가 추출되지 않았을 경우, 다른 방식으로 시도
        if (options.size() < expectedCount) {
            log.warn("번호 패턴으로 충분한 선택지를 추출하지 못했습니다. 다른 방식으로 시도합니다.");
            options = Arrays.stream(lines)
                .map(String::trim)
                .filter(line -> !line.isEmpty() && !line.endsWith("?"))  // 질문 형태 제외
                .limit(expectedCount)
                .collect(Collectors.toList());
        }
        
        // 선택지가 여전히 부족하면 기본 선택지 추가
        if (options.size() < expectedCount) {
            log.warn("충분한 선택지를 추출하지 못했습니다. 기본 선택지를 추가합니다.");
            List<String> defaultOptions = Arrays.asList(
                "Paris", "Tokyo", "New York", "London", "Rome",
                "Sydney", "Barcelona", "Seoul", "Venice", "Hawaii"
            );

            for (int i = options.size(); i < expectedCount && i < defaultOptions.size(); i++) {
                options.add(defaultOptions.get(i));
    }
        }

        // 요청한 개수만큼 반환 (부족하면 있는 만큼만)
        return options.stream()
            .limit(expectedCount)
            .collect(Collectors.toList());
    }

    /**
     * 오류 응답을 생성합니다.
     *
     * @param message 오류 메시지
     * @return 오류 응답
     */
    private GenerateOptionsResponse buildErrorResponse(String message) {
        return GenerateOptionsResponse.builder()
            .success(false)
            .message(message)
            .options(new ArrayList<>())
            .build();
    }

    /**
     * 제목과 유사한 가이드를 검색합니다.
     *
     * @param title 검색할 제목
     * @return 유사 가이드 검색 결과
     */
    @Override
    public SimilarGuidesResponse findSimilarGuides(String title) {
        log.info("유사 가이드 검색 시작 - 제목: {}", title);

        if (title == null || title.trim().isEmpty()) {
            log.warn("유사 가이드 검색 실패 - 제목이 비어있음");
            return SimilarGuidesResponse.builder()
                .success(false)
                .message("제목을 입력해주세요.")
                .guides(new ArrayList<>())
                .count(0)
                .build();
}

        try {
            // 키워드 서비스를 통해 유사한 가이드 검색
            List<KeywordGuideResponse> keywordGuides = keywordGuideService.searchGuidesByKeyword(title);

            // GuideSimpleInfo로 변환
            List<GuideSimpleInfo> guideInfos = keywordGuides.stream()
                .map(guide -> GuideSimpleInfo.builder()
                    .id(guide.getId())
                    .title(guide.getTitle())
                    .summary(createSummary(guide.getContent()))
                    .build())
                .collect(Collectors.toList());

            log.info("유사 가이드 검색 완료 - 제목: {}, 찾은 가이드 수: {}", title, guideInfos.size());

            return SimilarGuidesResponse.builder()
                .success(true)
                .message("유사한 가이드를 찾았습니다.")
                .guides(guideInfos)
                .count(guideInfos.size())
                .build();

        } catch (ResourceNotFoundException e) {
            log.info("유사 가이드 검색 결과 없음 - 제목: {}, 사유: {}", title, e.getMessage());
            return SimilarGuidesResponse.builder()
                .success(true)
                .message("유사한 가이드가 없습니다.")
                .guides(new ArrayList<>())
                .count(0)
                .build();
        } catch (Exception e) {
            log.error("유사 가이드 검색 중 오류 발생 - 제목: {}", title, e);
            return SimilarGuidesResponse.builder()
                .success(false)
                .message("가이드 검색 중 오류가 발생했습니다: " + e.getMessage())
                .guides(new ArrayList<>())
                .count(0)
                .build();
        }
    }

    /**
     * 가이드 내용에서 요약을 생성합니다.
     *
     * @param content 가이드 내용
     * @return 요약된 내용
     */
    private String createSummary(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }

        // 내용이 최대 길이보다 길면 잘라서 "..." 추가
        if (content.length() > MAX_SUMMARY_LENGTH) {
            return content.substring(0, MAX_SUMMARY_LENGTH) + "...";
        }

        return content;
    }
}

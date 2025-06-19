package pluto.upik.shared.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pluto.upik.shared.ai.data.DTO.GuideRequestDTO;
import pluto.upik.shared.ai.data.DTO.GuideResponseDTO;
import pluto.upik.shared.translation.service.TranslationService;

import jakarta.annotation.PostConstruct;

import java.time.LocalDate;
import java.util.*;

/**
 * AI Service
 * A service that generates content through DeepSeek AI.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AIService {
    private static final int MAX_CHUNK_SIZE = 450;
    
    @Value("${spring.ai.ollama.base-url}")
    private String ollamaBaseUrl;

    @Value("${spring.ai.ollama.chat.options.model}")
    private String ollamaModel;

    private final TranslationService translationService;
    private RestClient restClient;

    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder()
                .baseUrl(ollamaBaseUrl)
                .build();
    }

    private String removeThinkTags(String response) {
        if (response == null) {
            return null;
        }

        // <think> 태그 제거: 줄바꿈 포함, 대소문자 구분 없음
        return response.replaceAll("(?is)<think>.*?</think>", "").trim();
    }


    private List<String> splitTextIntoChunks(String text) {
        List<String> chunks = new ArrayList<>();
        
        // 문장 단위로 분리
        String[] sentences = text.split("(?<=[.!?]\\s)");
        StringBuilder currentChunk = new StringBuilder();
        
        for (String sentence : sentences) {
            if (currentChunk.length() + sentence.length() > MAX_CHUNK_SIZE) {
                if (currentChunk.length() > 0) {
                    chunks.add(currentChunk.toString().trim());
                    currentChunk = new StringBuilder();
                }
            }
            currentChunk.append(sentence);
        }
        
        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }
        
        return chunks;
    }

    private String translateLongText(String text, boolean koreanToEnglish) {
        List<String> chunks = splitTextIntoChunks(text);
        StringBuilder translatedText = new StringBuilder();
        
        for (String chunk : chunks) {
            String translatedChunk = koreanToEnglish ? 
                translationService.translateKoreanToEnglish(chunk) :
                translationService.translateEnglishToKorean(chunk);
            translatedText.append(translatedChunk).append(" ");
        }
        
        return translatedText.toString().trim();
    }

    public String askToDeepSeekAI(String question) {
        try {
            String translatedQuestion = translateLongText(question, true);
            Map<String, Object> requestBody = Map.of(
                    "model", ollamaModel,
                    "prompt", translatedQuestion,
                    "stream", false
            );

            Map<String, Object> response = restClient.post()
                    .uri("/api/generate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            if (response != null && response.containsKey("response")) {
                String englishResponse = (String) response.get("response");
                
                // 영어 응답에서 think 태그 제거
                String cleanedResponse = removeThinkTags(englishResponse);
                
                // 태그가 제거된 응답을 한국어로 번역
                return cleanedResponse;
            } else {
                throw new RuntimeException("AI 서비스로부터 유효한 응답을 받지 못했습니다.");
            }
        } catch (Exception e) {
            throw new RuntimeException("AI 서비스 호출 중 오류가 발생했습니다.", e);
        }
    }


    public GuideResponseDTO generateGuide(GuideRequestDTO requestDto) {
        String prompt = String.format(
                "Please generate a guide title and guide content for the following vote and responses. " +
                        "The guide should be clear, informative, and in-depth.\n\n" +
                        "Vote Title: %s\n" +
                        "Option with the highest votes : %s\n" +
                        "Voting Results (percentages):\n%s\n\n" +
                        "Tail Question: %s\n" +
                        "Tail Responses:\n%s\n\n" +
                        "Write it like this :\n%s\n\n"+
                        "Please return the result in the following format I will keep my word unconditionally:\n" +
                        "Guide Title:\n<<title>>\n\n" +
                        "Guide Content:\n<<content>>\n",
                requestDto.getVoteTitle(),
                requestDto.getVoteDescription(),
                requestDto.getOptionsWithPercents(),
                requestDto.getTailQuestion(),
                requestDto.getTailResponses(),
                requestDto.getType()
        );

        String result = askToDeepSeekAI(prompt);

        log.debug("result"+result);
        String extractedTitle = "";
        String extractedContent = "";

        int titleStart = result.indexOf("Guide Title:");
        int contentStart = result.indexOf("Guide Content:");

        if (titleStart != -1 && contentStart != -1) {
            extractedTitle = result.substring(titleStart + "Guide Title:".length(), contentStart).trim();
            extractedContent = result.substring(contentStart + "Guide Content:".length()).trim();
        } else {
            throw new RuntimeException("AI 응답 포맷이 예상과 다릅니다.");
        }

        return GuideResponseDTO.builder()
                .id(UUID.randomUUID())
                .voteId(requestDto.getVoteId())
                .title(translateLongText(extractedTitle,false))
                .content(translateLongText(extractedContent,false))
                .createdAt(LocalDate.now())
                .category("몰라 그 분류 AI쓰죠")
                .guideType(requestDto.getType())
                .revoteCount(0L)
                .like(0L)
                .build();
    }

}
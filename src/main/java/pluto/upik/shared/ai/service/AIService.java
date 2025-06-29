package pluto.upik.shared.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pluto.upik.domain.guide.data.model.Guide;
import pluto.upik.domain.guide.repository.GuideRepository;
import pluto.upik.domain.option.data.model.Option;
import pluto.upik.domain.option.repository.OptionRepository;
import pluto.upik.domain.tail.data.model.Tail;
import pluto.upik.domain.tail.repository.TailRepository;
import pluto.upik.domain.tail.repository.TailResponseRepository;
import pluto.upik.domain.user.repository.UserRepository;
import pluto.upik.domain.vote.data.model.Vote;
import pluto.upik.domain.vote.repository.VoteRepository;
import pluto.upik.domain.voteResponse.data.model.VoteResponse;
import pluto.upik.domain.voteResponse.repository.VoteResponseRepository;
import pluto.upik.shared.ai.config.ChatAiService;
import pluto.upik.shared.ai.data.DTO.GuideResponseDTO;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.exception.ResourceNotFoundException;
import pluto.upik.shared.translation.service.TranslationService;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIService {
    private static final int MAX_CHUNK_SIZE = 450;

    private final TranslationService translationService;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final VoteResponseRepository voteResponseRepository;
    private final OptionRepository optionRepository;
    private final TailRepository tailRepository;
    private final TailResponseRepository tailResponseRepository;
    private final ChatAiService chatAiService;
    private final GuideRepository guideRepository;

    private String removeThinkTags(String response) {
        if (response == null) return null;
        return response.replaceAll("(?is)<think>.*?</think>", "").trim();
    }

    private List<String> splitTextIntoChunks(String text) {
        List<String> chunks = new ArrayList<>();
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
            String translatedChunk = koreanToEnglish
                    ? translationService.translateKoreanToEnglish(chunk)
                    : translationService.translateEnglishToKorean(chunk);
            translatedText.append(translatedChunk).append(" ");
        }

        return translatedText.toString().trim();
    }

    private String askToDeepSeekAI(String question) {
        try {
            String translatedQuestion = translateLongText(question, true);
            String englishResponse = chatAiService.askToDeepSeekAI(translatedQuestion);
            return removeThinkTags(englishResponse);
        } catch (Exception e) {
            log.error("AI 서비스 호출 중 오류: {}", e.getMessage(), e);
            throw new BusinessException("AI 서비스 호출 중 오류가 발생했습니다.");
        }
    }

    @Transactional
    public GuideResponseDTO generateAndSaveGuide(UUID voteId, String type) {
        try {
            Vote vote = voteRepository.findById(voteId)
                    .orElseThrow(() -> new ResourceNotFoundException("투표를 찾을 수 없습니다."));

            String voteTitle = vote.getQuestion();
            String voteDescription = optionRepository.findTopByVoteOrderByIdAsc(vote)
                    .map(Option::getContent)
                    .orElse("No description");

            // voteId로 실제 투표 옵션 및 응답을 가져와서 퍼센트 계산
            List<Option> options = optionRepository.findByVoteId(vote.getId());
            if (options == null || options.isEmpty()) {
                throw new ResourceNotFoundException("투표 옵션이 존재하지 않습니다.");
            }
            List<VoteResponse> voteResponses = voteResponseRepository.findByVoteId(vote.getId());

            Map<UUID, Long> voteCounts = new HashMap<>();
            long totalVotes = voteResponses.size();

            for (VoteResponse vr : voteResponses) {
                voteCounts.merge(vr.getOption().getId(), 1L, Long::sum);
            }

            StringBuilder optionPercentsBuilder = new StringBuilder();
            for (Option option : options) {
                long count = voteCounts.getOrDefault(option.getId(), 0L);
                double percent = totalVotes > 0 ? (count * 100.0 / totalVotes) : 0.0;
                optionPercentsBuilder
                        .append(option.getContent())
                        .append(" - ")
                        .append(String.format("%.1f", percent))
                        .append("%\n");
            }
            String optionsWithPercents = optionPercentsBuilder.toString().trim();

            Tail tail = tailRepository.findFirstByVote(vote)
                    .orElseThrow(() -> new ResourceNotFoundException("Tail 질문이 없습니다."));

            List<String> tailAnswers = tailResponseRepository.findByTail(tail).stream()
                    .map(tr -> tr.getAnswer())
                    .toList();

            if (tailAnswers.isEmpty()) {
                throw new ResourceNotFoundException("Tail 답변이 존재하지 않습니다.");
            }

            String tailResponses = String.join("\n", tailAnswers);

            String prompt = String.format(
                    "Please generate a guide title and guide content for the following vote and responses. " +
                            "The guide should be clear, informative, and in-depth.\n\n" +
                            "Vote Title: %s\n" +
                            "Option with the highest votes : %s\n" +
                            "Voting Results (percentages):\n%s\n\n" +
                            "Tail Question: %s\n" +
                            "Tail Responses:\n%s\n\n" +
                            "Write it like this :\n%s\n\n" +
                            "Please return the result in the following format I will keep my word unconditionally:\n" +
                            "Guide Title:\n<<title>>\n\n" +
                            "Guide Content:\n<<content>>\n",
                    voteTitle, voteDescription, optionsWithPercents,
                    tail.getQuestion(), tailResponses, type
            );

            String result = askToDeepSeekAI(prompt);
            int titleStart = result.indexOf("Guide Title:");
            int contentStart = result.indexOf("Guide Content:");

            if (titleStart == -1 || contentStart == -1) {
                log.error("AI 응답 포맷이 예상과 다릅니다. result: {}", result);
                throw new BusinessException("AI 응답 포맷이 예상과 다릅니다.");
            }

            String extractedTitle = result.substring(titleStart + "Guide Title:".length(), contentStart).trim();
            String extractedContent = result.substring(contentStart + "Guide Content:".length()).trim();

            Guide guide = Guide.builder()
                    .vote(vote)
                    .title(translateLongText(extractedTitle, false))
                    .content(translateLongText(extractedContent, false))
                    .createdAt(LocalDate.now())
                    .category(vote.getCategory())
                    .guideType(type)
                    .revoteCount(0L)
                    .like(0L)
                    .build();

            guideRepository.save(guide);

            // GuideResponseDTO 형식으로 반환
            return GuideResponseDTO.builder()
                    .id(guide.getId())
                    .voteId(guide.getVote().getId())
                    .title(guide.getTitle())
                    .content(guide.getContent())
                    .createdAt(guide.getCreatedAt())
                    .category(guide.getCategory())
                    .guideType(type)
                    .revoteCount(guide.getRevoteCount())
                    .like(guide.getLike())
                    .build();
        } catch (ResourceNotFoundException | BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("가이드 생성 중 알 수 없는 오류: {}", e.getMessage(), e);
            throw new BusinessException("가이드 생성 중 오류가 발생했습니다.");
        }
    }
}

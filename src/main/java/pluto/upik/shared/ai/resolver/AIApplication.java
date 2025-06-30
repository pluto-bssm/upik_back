package pluto.upik.shared.ai.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pluto.upik.domain.vote.data.model.Vote;
import pluto.upik.domain.vote.repository.VoteRepository;
import pluto.upik.shared.ai.service.AIService;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AIApplication {
    private final AIService aiService;
    private final VoteRepository voteRepository;

    // 매일 밤 12시에 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void runEveryMidnight() {
        // 오늘 이전에 끝났고 상태가 OPEN인 투표만 조회
        List<Vote> expiredVotes = voteRepository.findFinishedVotesWithoutGuide(LocalDate.now());

        for (Vote vote : expiredVotes) {
            aiService.generateAndSaveGuide(vote.getId(), vote.getCategory()); // 필요시 타입 지정
        }
    }
}

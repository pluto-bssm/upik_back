package pluto.upik.shared.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 투표 스케줄러
 * 주기적으로 투표를 확인하고 필요한 작업을 수행합니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VoteScheduler {

    //private final VoteService voteService;

    /**
     * 매일 자정에 종료된 투표를 확인하고 가이드를 생성합니다.
     * cron 표현식: 초 분 시 일 월 요일
     * 0 0 0 * * * = 매일 자정(00:00:00)
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void checkFinishedVotes() {
        log.info("종료된 투표 확인 스케줄러 실행");
        try {
           // int processedCount = voteService.processFinishedVotes();
            //log.info("스케줄러 실행 완료. 처리된 투표 수: {}", processedCount);
        } catch (Exception e) {
            log.error("스케줄러 실행 중 오류 발생", e);
        }
    }
}
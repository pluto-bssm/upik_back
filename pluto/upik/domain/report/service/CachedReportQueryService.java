package pluto.upik.domain.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import pluto.upik.domain.report.data.DTO.ReportResponse;

import java.util.List;
import java.util.UUID;

/**
 * 캐싱을 적용한 신고 조회 서비스
 * 기존 ReportQueryService를 래핑하고 캐싱 기능을 추가합니다.
 */
@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class CachedReportQueryService {

    private final ReportQueryService reportQueryService;

    /**
     * 사용자가 작성한 신고 목록을 조회하고 결과를 캐싱합니다.
     *
     * @param userId 사용자 ID
     * @return 신고 응답 목록
     */
    @Cacheable(value = "userReports", key = "#userId")
    public List<ReportResponse> getReportsByUser(UUID userId) {
        log.debug("사용자별 신고 캐시 조회 시도 - userId: {}", userId);
        return reportQueryService.getReportsByUser(userId);
    }

    /**
     * 특정 대상에 대한 신고 목록을 조회하고 결과를 캐싱합니다.
     *
     * @param targetId 대상 ID
     * @return 신고 응답 목록
     */
    @Cacheable(value = "targetReports", key = "#targetId")
    public List<ReportResponse> getReportsByTarget(UUID targetId) {
        log.debug("대상별 신고 캐시 조회 시도 - targetId: {}", targetId);
        return reportQueryService.getReportsByTarget(targetId);
    }

    /**
     * 모든 신고 목록을 조회하고 결과를 캐싱합니다.
     *
     * @return 신고 응답 목록
     */
    @Cacheable(value = "reports")
    public List<ReportResponse> getAllReports() {
        log.debug("모든 신고 캐시 조회 시도");
        return reportQueryService.getAllReports();
    }
}
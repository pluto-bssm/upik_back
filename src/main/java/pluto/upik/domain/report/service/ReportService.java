package pluto.upik.domain.report.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pluto.upik.domain.guide.repository.GuideRepository;
import pluto.upik.domain.report.data.DTO.ReportResponse;
import pluto.upik.domain.report.data.model.Report;
import pluto.upik.domain.report.repository.ReportRepository;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.exception.ResourceNotFoundException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;
    private final GuideRepository guideRepository;

    @Transactional
    public void deleteReport(UUID userId, UUID targetId) {
        log.info("신고 삭제 요청 시작 - userId: {}, targetId: {}", userId, targetId);

        try {
            // 존재 여부 확인
            if (!reportRepository.existsByUserIdAndTargetId(userId, targetId)) {
                log.warn("신고 삭제 실패 - 신고가 존재하지 않음 (userId: {}, targetId: {})", userId, targetId);
                throw new ResourceNotFoundException("해당 신고가 존재하지 않습니다.");
            }

            // 삭제
            reportRepository.deleteByUserIdAndTargetId(userId, targetId);
            log.info("신고 삭제 성공 - 신고 데이터 삭제 완료 (userId: {}, targetId: {})", userId, targetId);

            // 가이드의 revote 카운트 감소
            try {
                guideRepository.decrementRevoteCount(targetId);
                log.info("가이드 revote 카운트 감소 완료 - targetId: {}", targetId);
            } catch (Exception e) {
                log.error("가이드 revote 카운트 감소 중 오류 - targetId: {}, error: {}", targetId, e.getMessage(), e);
                throw new BusinessException("가이드 revote 카운트 감소 중 오류가 발생했습니다.");
            }
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("신고 삭제 중 알 수 없는 오류 - userId: {}, targetId: {}, error: {}", userId, targetId, e.getMessage(), e);
            throw new BusinessException("신고 삭제 중 오류가 발생했습니다.");
        }
    }

    // 특정 사용자가 신고한 목록 조회
    public List<ReportResponse> getReportsByUser(UUID userId) {
        log.info("사용자 신고 목록 조회 요청 시작 - userId: {}", userId);

        try {
            List<Report> reportList = reportRepository.findByUserId(userId);

            if (reportList == null || reportList.isEmpty()) {
                throw new ResourceNotFoundException("해당 사용자가 작성한 신고가 없습니다.");
            }

            List<ReportResponse> reports = reportList.stream()
                    .map(this::mapToReportResponse)
                    .collect(Collectors.toList());

            log.info("사용자 신고 목록 조회 완료 - userId: {}, 결과 개수: {}", userId, reports.size());
            return reports;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("사용자 신고 목록 조회 중 오류 - userId: {}, error: {}", userId, e.getMessage(), e);
            throw new BusinessException("사용자 신고 목록 조회 중 오류가 발생했습니다.");
        }
    }

    // 특정 신고 대상의 신고 목록 조회
    public List<ReportResponse> getReportsByTarget(UUID targetId) {
        log.info("신고 대상 목록 조회 요청 시작 - targetId: {}", targetId);

        try {
            List<Report> reportList = reportRepository.findByTargetId(targetId);

            if (reportList == null || reportList.isEmpty()) {
                throw new ResourceNotFoundException("해당 대상에 대한 신고가 없습니다.");
            }

            List<ReportResponse> reports = reportList.stream()
                    .map(this::mapToReportResponse)
                    .collect(Collectors.toList());

            log.info("신고 대상 목록 조회 완료 - targetId: {}, 결과 개수: {}", targetId, reports.size());
            return reports;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("신고 대상 목록 조회 중 오류 - targetId: {}, error: {}", targetId, e.getMessage(), e);
            throw new BusinessException("신고 대상 목록 조회 중 오류가 발생했습니다.");
        }
    }

    // 모든 신고 목록 조회
    public List<ReportResponse> getAllReports() {
        log.info("모든 신고 목록 조회 요청 시작");

        try {
            List<Report> reportList = reportRepository.findAll();

            if (reportList == null || reportList.isEmpty()) {
                throw new ResourceNotFoundException("신고 내역이 존재하지 않습니다.");
            }

            List<ReportResponse> reports = reportList.stream()
                    .map(this::mapToReportResponse)
                    .collect(Collectors.toList());

            log.info("모든 신고 목록 조회 완료 - 결과 개수: {}", reports.size());
            return reports;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("모든 신고 목록 조회 중 오류 - error: {}", e.getMessage(), e);
            throw new BusinessException("모든 신고 목록 조회 중 오류가 발생했습니다.");
        }
    }

    // Report 엔티티를 ReportResponse로 변환
    private ReportResponse mapToReportResponse(Report report) {
        log.debug("Report 엔티티를 ReportResponse로 변환 중 - reportId: {}", report.getUserId());

        ReportResponse response = new ReportResponse(
                report.getUserId(),
                report.getTargetId(),
                report.getReason(),
                report.getCreatedAt()
        );

        log.debug("Report 엔티티 변환 완료 - reportId: {}, response: {}", report.getUserId(), response);
        return response;
    }
}

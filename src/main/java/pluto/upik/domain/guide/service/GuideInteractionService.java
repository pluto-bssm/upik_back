package pluto.upik.domain.guide.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import pluto.upik.domain.guide.data.model.GuideAndUser;
import pluto.upik.domain.guide.data.model.GuideAndUserId;
import pluto.upik.domain.guide.repository.GuideAndUserRepository;
import pluto.upik.domain.guide.repository.GuideRepository;
import pluto.upik.domain.report.data.model.Report;
import pluto.upik.domain.report.repository.ReportRepository;
import pluto.upik.domain.user.repository.UserRepository;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GuideInteractionService {

    private final GuideRepository guideRepository;
    private final ReportRepository reportRepository;
    private final GuideAndUserRepository guideAndUserRepository;
    private final UserRepository userRepository;

    /**
     * 특정 유저가 특정 가이드에 좋아요 토글 기능.
     * 이미 좋아요 되어있으면 좋아요 취소(삭제) 후 카운트 감소
     * 좋아요 없으면 저장 후 카운트 증가
     */
    public boolean toggleLikeGuide(UUID userId, UUID guideId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
        if (!guideRepository.existsById(guideId)) {
            throw new ResourceNotFoundException("Guide not found: " + guideId);
        }

        GuideAndUserId id = new GuideAndUserId(userId, guideId);
        try {
            if (guideAndUserRepository.existsById(id)) {
                guideAndUserRepository.deleteById(id);
                guideRepository.decrementLikeCount(guideId);
                return false;
            } else {
                GuideAndUser entity = new GuideAndUser();
                entity.setId(id);
                guideAndUserRepository.save(entity);
                guideRepository.incrementLikeCount(guideId);
                return true;
            }
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Data integrity violation: " + e.getMessage());
        } catch (Exception e) {
            throw new BusinessException("Unexpected error: " + e.getMessage());
        }
    }


    /**
     * 특정 유저가 특정 가이드에 대해 재투표 신고 토글 기능.
     * 이미 신고 되어있으면 신고 취소(삭제) 후 revote count 감소
     * 신고 안되어 있으면 저장 후 revote count 증가
     */
    public boolean toggleReportAndRevote(UUID guideId, UUID userId, String reason) {
        // 유저/가이드 존재 체크
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
        if (!guideRepository.existsById(guideId)) {
            throw new ResourceNotFoundException("Guide not found: " + guideId);
        }

        try {
            boolean exists = reportRepository.existsByUserIdAndTargetId(userId, guideId);
            if (exists) {
                // 신고 취소
                reportRepository.deleteByUserIdAndTargetId(userId, guideId);
                guideRepository.decrementRevoteCount(guideId);
                log.info("User {} canceled report on Guide {}", userId, guideId);
                return false;
            } else {
                // 신고 추가
                reportRepository.save(Report.builder()
                        .userId(userId)
                        .targetId(guideId)
                        .reason(reason)
                        .createdAt(LocalDate.now())
                        .build());

                guideRepository.incrementRevoteCount(guideId);
                log.info("User {} reported Guide {}", userId, guideId);
                return true;
            }
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation during toggleReportAndRevote: userId={}, guideId={}, reason={}", userId, guideId, reason, e);
            throw new BusinessException("데이터 무결성 위반: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during toggleReportAndRevote: userId={}, guideId={}, reason={}", userId, guideId, reason, e);
            throw new BusinessException("알 수 없는 오류가 발생했습니다.");
        }
    }

}

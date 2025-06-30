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
import pluto.upik.shared.util.ValidationUtils;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 가이드 상호작용(좋아요, 신고 등) 관련 비즈니스 로직을 처리하는 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GuideInteractionService implements GuideInteractionServiceInterface {

    private final GuideRepository guideRepository;
    private final ReportRepository reportRepository;
    private final GuideAndUserRepository guideAndUserRepository;
    private final UserRepository userRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean toggleLikeGuide(UUID userId, UUID guideId) {
        log.debug("가이드 좋아요 토글 시작 - userId: {}, guideId: {}", userId, guideId);
        
        // 사용자 존재 확인
        validateUserAndGuideExist(userId, guideId);

        GuideAndUserId id = new GuideAndUserId(userId, guideId);
        try {
            // 이미 좋아요 했는지 확인
            boolean exists = guideAndUserRepository.existsById(id);
            
            if (exists) {
                // 좋아요 취소
                guideAndUserRepository.deleteById(id);
                guideRepository.decrementLikeCount(guideId);
                log.info("가이드 좋아요 취소 완료 - userId: {}, guideId: {}", userId, guideId);
                return false;
            } else {
                // 좋아요 추가
                GuideAndUser entity = new GuideAndUser();
                entity.setId(id);
                guideAndUserRepository.save(entity);
                guideRepository.incrementLikeCount(guideId);
                log.info("가이드 좋아요 추가 완료 - userId: {}, guideId: {}", userId, guideId);
                return true;
            }
        } catch (DataIntegrityViolationException e) {
            log.error("가이드 좋아요 토글 중 데이터 무결성 위반 - userId: {}, guideId: {}, error: {}", 
                    userId, guideId, e.getMessage());
            throw new BusinessException("데이터 무결성 위반: " + e.getMessage());
        } catch (Exception e) {
            log.error("가이드 좋아요 토글 중 예상치 못한 오류 - userId: {}, guideId: {}, error: {}", 
                    userId, guideId, e.getMessage());
            throw new BusinessException("가이드 좋아요 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean toggleReportAndRevote(UUID userId, UUID guideId, String reason) {
        log.debug("가이드 재투표 신고 토글 시작 - userId: {}, guideId: {}, reason: {}", userId, guideId, reason);
        
        // 사용자와 가이드 존재 확인
        validateUserAndGuideExist(userId, guideId);

        try {
            // 이미 신고했는지 확인
            boolean exists = reportRepository.existsByUserIdAndTargetId(userId, guideId);
            
            if (exists) {
                // 신고 취소
                reportRepository.deleteByUserIdAndTargetId(userId, guideId);
                guideRepository.decrementRevoteCount(guideId);
                log.info("가이드 재투표 신고 취소 완료 - userId: {}, guideId: {}", userId, guideId);
                return false;
            } else {
                // 신고 추가
                Report report = Report.builder()
                        .userId(userId)
                        .targetId(guideId)
                        .reason(reason)
                        .createdAt(LocalDate.now())
                        .build();
                reportRepository.save(report);
                guideRepository.incrementRevoteCount(guideId);
                log.info("가이드 재투표 신고 추가 완료 - userId: {}, guideId: {}, reason: {}", userId, guideId, reason);
                return true;
            }
        } catch (DataIntegrityViolationException e) {
            log.error("가이드 재투표 신고 토글 중 데이터 무결성 위반 - userId: {}, guideId: {}, reason: {}, error: {}", 
                    userId, guideId, reason, e.getMessage());
            throw new BusinessException("데이터 무결성 위반: " + e.getMessage());
        } catch (Exception e) {
            log.error("가이드 재투표 신고 토글 중 예상치 못한 오류 - userId: {}, guideId: {}, reason: {}, error: {}", 
                    userId, guideId, reason, e.getMessage());
            throw new BusinessException("가이드 재투표 신고 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 사용자와 가이드가 존재하는지 확인합니다.
     * 
     * @param userId 사용자 ID
     * @param guideId 가이드 ID
     * @throws ResourceNotFoundException 사용자 또는 가이드가 존재하지 않는 경우
     */
    private void validateUserAndGuideExist(UUID userId, UUID guideId) {
        // 사용자 존재 확인
        boolean userExists = userRepository.existsById(userId);
        ValidationUtils.validateResourceExists(userExists, "사용자", userId);
        
        // 가이드 존재 확인
        boolean guideExists = guideRepository.existsById(guideId);
        ValidationUtils.validateResourceExists(guideExists, "가이드", guideId);
    }
}
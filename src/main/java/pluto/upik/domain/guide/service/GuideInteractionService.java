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
            log.warn("User ID {} does not exist", userId);
            return false;
        }
        if (!guideRepository.existsById(guideId)) {
            log.warn("Guide ID {} does not exist", guideId);
            return false;
        }

        GuideAndUserId id = new GuideAndUserId(userId, guideId);
        try {
            if (guideAndUserRepository.existsById(id)) {
                guideAndUserRepository.deleteById(id);
                int updatedRows = guideRepository.decrementLikeCount(guideId);
                log.info("User {} canceled like on Guide {}", userId, guideId);
                return false;
            } else {
                GuideAndUser entity = new GuideAndUser();
                entity.setId(id);
                guideAndUserRepository.save(entity);
                int updatedRows = guideRepository.incrementLikeCount(guideId);
                log.info("User {} liked Guide {}", userId, guideId);
                return true;
            }
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation when user {} toggles like on guide {}: {}", userId, guideId, e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Unexpected error when user {} toggles like on guide {}: {}", userId, guideId, e.getMessage(), e);
            return false;
        }
    }


    /**
     * 특정 유저가 특정 가이드에 대해 재투표 신고 토글 기능.
     * 이미 신고 되어있으면 신고 취소(삭제) 후 revote count 감소
     * 신고 안되어 있으면 저장 후 revote count 증가
     */
    public boolean toggleReportAndRevote(UUID guideId, UUID userId, String reason) {
        GuideAndUserId id = new GuideAndUserId(userId, guideId);

        try {
            boolean exists = reportRepository.existsByUserIdAndTargetId(userId, guideId);
            if (exists) {
                // 신고 취소
                reportRepository.deleteByUserIdAndTargetId(userId, guideId);
                int updatedRows = guideRepository.decrementRevoteCount(guideId);
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

                int updatedRows = guideRepository.incrementRevoteCount(guideId);
                log.info("User {} reported Guide {}", userId, guideId);
                return true;
            }
        } catch (Exception e) {
            log.error("Unexpected error during toggleReportAndRevote: userId={}, guideId={}, reason={}", userId, guideId, reason, e);
            return false;
        }
    }
}

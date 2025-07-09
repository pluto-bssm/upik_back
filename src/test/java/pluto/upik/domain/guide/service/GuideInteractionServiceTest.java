package pluto.upik.domain.guide.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import pluto.upik.domain.guide.data.model.GuideAndUser;
import pluto.upik.domain.guide.data.model.GuideAndUserId;
import pluto.upik.domain.guide.repository.GuideAndUserRepository;
import pluto.upik.domain.guide.repository.GuideRepository;
import pluto.upik.domain.report.data.model.Report;
import pluto.upik.domain.report.repository.ReportRepository;
import pluto.upik.domain.user.repository.UserRepository;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.exception.ResourceNotFoundException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * GuideInteractionService 클래스에 대한 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class GuideInteractionServiceTest {

    @Mock
    private GuideRepository guideRepository;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private GuideAndUserRepository guideAndUserRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GuideInteractionService guideInteractionService;

    private UUID userId;
    private UUID guideId;
    private GuideAndUserId guideAndUserId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        guideId = UUID.randomUUID();
        guideAndUserId = new GuideAndUserId(userId, guideId);
    }

    @Test
    @DisplayName("좋아요 토글 - 좋아요 추가 테스트")
    void toggleLikeGuide_AddLike() {
        // given
        when(userRepository.existsById(userId)).thenReturn(true);
        when(guideRepository.existsById(guideId)).thenReturn(true);
        when(guideAndUserRepository.existsById(guideAndUserId)).thenReturn(false);
        when(guideAndUserRepository.save(any(GuideAndUser.class))).thenReturn(new GuideAndUser());
        when(guideRepository.incrementLikeCount(guideId)).thenReturn(1);

        // when
        boolean result = guideInteractionService.toggleLikeGuide(userId, guideId);

        // then
        assertTrue(result);
        verify(userRepository).existsById(userId);
        verify(guideRepository).existsById(guideId);
        verify(guideAndUserRepository).existsById(guideAndUserId);
        verify(guideAndUserRepository).save(any(GuideAndUser.class));
        verify(guideRepository).incrementLikeCount(guideId);
    }

    @Test
    @DisplayName("좋아요 토글 - 좋아요 취소 테스트")
    void toggleLikeGuide_RemoveLike() {
        // given
        when(userRepository.existsById(userId)).thenReturn(true);
        when(guideRepository.existsById(guideId)).thenReturn(true);
        when(guideAndUserRepository.existsById(guideAndUserId)).thenReturn(true);
        doNothing().when(guideAndUserRepository).deleteById(guideAndUserId);
        when(guideRepository.decrementLikeCount(guideId)).thenReturn(1);

        // when
        boolean result = guideInteractionService.toggleLikeGuide(userId, guideId);

        // then
        assertFalse(result);
        verify(userRepository).existsById(userId);
        verify(guideRepository).existsById(guideId);
        verify(guideAndUserRepository).existsById(guideAndUserId);
        verify(guideAndUserRepository).deleteById(guideAndUserId);
        verify(guideRepository).decrementLikeCount(guideId);
    }

    @Test
    @DisplayName("좋아요 토글 - 사용자가 존재하지 않는 경우 테스트")
    void toggleLikeGuide_UserNotFound() {
        // given
        when(userRepository.existsById(userId)).thenReturn(false);

        // when & then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> guideInteractionService.toggleLikeGuide(userId, guideId));
            
        assertEquals("User not found: " + userId, exception.getMessage());
        verify(userRepository).existsById(userId);
        verify(guideRepository, never()).existsById(any());
    }

    @Test
    @DisplayName("좋아요 토글 - 가이드가 존재하지 않는 경우 테스트")
    void toggleLikeGuide_GuideNotFound() {
        // given
        when(userRepository.existsById(userId)).thenReturn(true);
        when(guideRepository.existsById(guideId)).thenReturn(false);

        // when & then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> guideInteractionService.toggleLikeGuide(userId, guideId));
            
        assertEquals("Guide not found: " + guideId, exception.getMessage());
        verify(userRepository).existsById(userId);
        verify(guideRepository).existsById(guideId);
    }

    @Test
    @DisplayName("좋아요 토글 - 데이터 무결성 위반 테스트")
    void toggleLikeGuide_DataIntegrityViolation() {
        // given
        when(userRepository.existsById(userId)).thenReturn(true);
        when(guideRepository.existsById(guideId)).thenReturn(true);
        when(guideAndUserRepository.existsById(guideAndUserId)).thenReturn(false);
        when(guideAndUserRepository.save(any(GuideAndUser.class))).thenThrow(new DataIntegrityViolationException("데이터 무결성 위반"));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> guideInteractionService.toggleLikeGuide(userId, guideId));
            
        assertTrue(exception.getMessage().contains("Data integrity violation"));
        verify(userRepository).existsById(userId);
        verify(guideRepository).existsById(guideId);
        verify(guideAndUserRepository).existsById(guideAndUserId);
        verify(guideAndUserRepository).save(any(GuideAndUser.class));
    }

    @Test
    @DisplayName("재투표 신고 토글 - 신고 추가 테스트")
    void toggleReportAndRevote_AddReport() {
        // given
        String reason = "테스트 신고 사유";
        when(userRepository.existsById(userId)).thenReturn(true);
        when(guideRepository.existsById(guideId)).thenReturn(true);
        when(reportRepository.existsByUserIdAndTargetId(userId, guideId)).thenReturn(false);
        when(reportRepository.save(any(Report.class))).thenReturn(new Report());
        when(guideRepository.incrementRevoteCount(guideId)).thenReturn(1);

        // when
        boolean result = guideInteractionService.toggleReportAndRevote(guideId, userId, reason);

        // then
        assertTrue(result);
        verify(userRepository).existsById(userId);
        verify(guideRepository).existsById(guideId);
        verify(reportRepository).existsByUserIdAndTargetId(userId, guideId);
        verify(reportRepository).save(any(Report.class));
        verify(guideRepository).incrementRevoteCount(guideId);
    }

    @Test
    @DisplayName("재투표 신고 토글 - 신고 취소 테스트")
    void toggleReportAndRevote_RemoveReport() {
        // given
        String reason = "테스트 신고 사유";
        when(userRepository.existsById(userId)).thenReturn(true);
        when(guideRepository.existsById(guideId)).thenReturn(true);
        when(reportRepository.existsByUserIdAndTargetId(userId, guideId)).thenReturn(true);
        doNothing().when(reportRepository).deleteByUserIdAndTargetId(userId, guideId);
        when(guideRepository.decrementRevoteCount(guideId)).thenReturn(1);

        // when
        boolean result = guideInteractionService.toggleReportAndRevote(guideId, userId, reason);

        // then
        assertFalse(result);
        verify(userRepository).existsById(userId);
        verify(guideRepository).existsById(guideId);
        verify(reportRepository).existsByUserIdAndTargetId(userId, guideId);
        verify(reportRepository).deleteByUserIdAndTargetId(userId, guideId);
        verify(guideRepository).decrementRevoteCount(guideId);
    }

    @Test
    @DisplayName("재투표 신고 토글 - 사용자가 존재하지 않는 경우 테스트")
    void toggleReportAndRevote_UserNotFound() {
        // given
        String reason = "테스트 신고 사유";
        when(userRepository.existsById(userId)).thenReturn(false);

        // when & then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> guideInteractionService.toggleReportAndRevote(guideId, userId, reason));
            
        assertEquals("User not found: " + userId, exception.getMessage());
        verify(userRepository).existsById(userId);
        verify(guideRepository, never()).existsById(any());
    }

    @Test
    @DisplayName("재투표 신고 토글 - 가이드가 존재하지 않는 경우 테스트")
    void toggleReportAndRevote_GuideNotFound() {
        // given
        String reason = "테스트 신고 사유";
        when(userRepository.existsById(userId)).thenReturn(true);
        when(guideRepository.existsById(guideId)).thenReturn(false);

        // when & then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> guideInteractionService.toggleReportAndRevote(guideId, userId, reason));
            
        assertEquals("Guide not found: " + guideId, exception.getMessage());
        verify(userRepository).existsById(userId);
        verify(guideRepository).existsById(guideId);
    }

    @Test
    @DisplayName("재투표 신고 토글 - 데이터 무결성 위반 테스트")
    void toggleReportAndRevote_DataIntegrityViolation() {
        // given
        String reason = "테스트 신고 사유";
        when(userRepository.existsById(userId)).thenReturn(true);
        when(guideRepository.existsById(guideId)).thenReturn(true);
        when(reportRepository.existsByUserIdAndTargetId(userId, guideId)).thenReturn(false);
        when(reportRepository.save(any(Report.class))).thenThrow(new DataIntegrityViolationException("데이터 무결성 위반"));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> guideInteractionService.toggleReportAndRevote(guideId, userId, reason));
            
        assertTrue(exception.getMessage().contains("데이터 무결성 위반"));
        verify(userRepository).existsById(userId);
        verify(guideRepository).existsById(guideId);
        verify(reportRepository).existsByUserIdAndTargetId(userId, guideId);
        verify(reportRepository).save(any(Report.class));
    }
}
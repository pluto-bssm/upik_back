package pluto.upik.domain.guide.resolver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pluto.upik.domain.guide.data.DTO.GuideMutation;
import pluto.upik.domain.guide.service.GuideInteractionServiceInterface;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.exception.ResourceNotFoundException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * GuideMutationResolver 클래스에 대한 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class GuideMutationResolverTest {

    @Mock
    private GuideInteractionServiceInterface guideInteractionService;

    @InjectMocks
    private GuideMutationResolver guideMutationResolver;

    private UUID guideId;
    private String guideIdString;
    private GuideMutation parent;
    private UUID dummyUserId;

    @BeforeEach
    void setUp() {
        guideId = UUID.fromString("e49207e8-471a-11f0-937c-42010a800004");
        guideIdString = guideId.toString();
        parent = new GuideMutation();
        dummyUserId = UUID.fromString("e49207e8-471a-11f0-937c-42010a800003");
    }

    @Test
    @DisplayName("가이드 좋아요 증가 테스트")
    void incrementGuideLike_Success() {
        // given
        when(guideInteractionService.toggleLikeGuide(eq(dummyUserId), eq(guideId))).thenReturn(true);

        // when
        boolean result = guideMutationResolver.incrementGuideLike(parent, guideIdString);

        // then
        assertTrue(result);
        verify(guideInteractionService).toggleLikeGuide(eq(dummyUserId), eq(guideId));
    }

    @Test
    @DisplayName("가이드 좋아요 취소 테스트")
    void incrementGuideLike_Cancel() {
        // given
        when(guideInteractionService.toggleLikeGuide(eq(dummyUserId), eq(guideId))).thenReturn(false);

        // when
        boolean result = guideMutationResolver.incrementGuideLike(parent, guideIdString);

        // then
        assertFalse(result);
        verify(guideInteractionService).toggleLikeGuide(eq(dummyUserId), eq(guideId));
    }
    
    @Test
    @DisplayName("가이드 좋아요 - 잘못된 UUID 형식 테스트")
    void incrementGuideLike_InvalidUUID() {
        // given
        String invalidId = "invalid-uuid";

        // when & then
        assertThrows(IllegalArgumentException.class, () -> 
            guideMutationResolver.incrementGuideLike(parent, invalidId));
    }
    
    @Test
    @DisplayName("가이드 좋아요 - 리소스 없음 예외 테스트")
    void incrementGuideLike_ResourceNotFound() {
        // given
        when(guideInteractionService.toggleLikeGuide(eq(dummyUserId), eq(guideId)))
            .thenThrow(new ResourceNotFoundException("Guide not found: " + guideId));

        // when & then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> 
            guideMutationResolver.incrementGuideLike(parent, guideIdString));
            
        assertEquals("Guide not found: " + guideId, exception.getMessage());
        verify(guideInteractionService).toggleLikeGuide(eq(dummyUserId), eq(guideId));
    }

    @Test
    @DisplayName("가이드 재투표 신고 테스트")
    void incrementGuideRevote_Success() {
        // given
        String reason = "테스트 신고 사유";
        when(guideInteractionService.toggleReportAndRevote(eq(guideId), eq(dummyUserId), eq(reason))).thenReturn(true);

        // when
        boolean result = guideMutationResolver.incrementGuideRevote(parent, guideIdString, reason);

        // then
        assertTrue(result);
        verify(guideInteractionService).toggleReportAndRevote(eq(guideId), eq(dummyUserId), eq(reason));
    }

    @Test
    @DisplayName("가이드 재투표 신고 취소 테스트")
    void incrementGuideRevote_Cancel() {
        // given
        String reason = "테스트 신고 사유";
        when(guideInteractionService.toggleReportAndRevote(eq(guideId), eq(dummyUserId), eq(reason))).thenReturn(false);

        // when
        boolean result = guideMutationResolver.incrementGuideRevote(parent, guideIdString, reason);

        // then
        assertFalse(result);
        verify(guideInteractionService).toggleReportAndRevote(eq(guideId), eq(dummyUserId), eq(reason));
    }
    
    @Test
    @DisplayName("가이드 재투표 신고 - 잘못된 UUID 형식 테스트")
    void incrementGuideRevote_InvalidUUID() {
        // given
        String invalidId = "invalid-uuid";
        String reason = "테스트 신고 사유";

        // when & then
        assertThrows(IllegalArgumentException.class, () -> 
            guideMutationResolver.incrementGuideRevote(parent, invalidId, reason));
    }
    
    @Test
    @DisplayName("가이드 재투표 신고 - 비즈니스 예외 테스트")
    void incrementGuideRevote_BusinessException() {
        // given
        String reason = "테스트 신고 사유";
        when(guideInteractionService.toggleReportAndRevote(eq(guideId), eq(dummyUserId), eq(reason)))
            .thenThrow(new BusinessException("데이터 무결성 위반"));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> 
            guideMutationResolver.incrementGuideRevote(parent, guideIdString, reason));
            
        assertEquals("데이터 무결성 위반", exception.getMessage());
        verify(guideInteractionService).toggleReportAndRevote(eq(guideId), eq(dummyUserId), eq(reason));
    }
}
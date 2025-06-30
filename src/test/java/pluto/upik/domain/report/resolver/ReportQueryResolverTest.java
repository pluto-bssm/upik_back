package pluto.upik.domain.report.resolver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pluto.upik.domain.report.application.ReportApplicationInterface;
import pluto.upik.domain.report.data.DTO.ReportResponse;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ReportQueryResolver 클래스에 대한 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class ReportQueryResolverTest {

    @Mock
    private ReportApplicationInterface reportApplication;

    @InjectMocks
    private ReportQueryResolver reportQueryResolver;

    private UUID userId;
    private UUID targetId;
    private ReportResponse testReportResponse;
    private List<ReportResponse> reportResponseList;
    private Object parent;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        targetId = UUID.randomUUID();
        testReportResponse = ReportResponse.builder()
                .userId(userId)
                .targetId(targetId)
                .reason("테스트 신고 사유")
                .createdAt(LocalDate.now())
                .build();
        reportResponseList = Arrays.asList(testReportResponse);
        parent = new Object();
    }

    @Test
    @DisplayName("사용자별 신고 목록 조회 성공 테스트")
    void getReportsByUser_Success() {
        // given
        when(reportApplication.getReportsByUser(userId)).thenReturn(reportResponseList);

        // when
        List<ReportResponse> result = reportQueryResolver.getReportsByUser(parent, userId);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());
        assertEquals(targetId, result.get(0).getTargetId());
        assertEquals("테스트 신고 사유", result.get(0).getReason());
        verify(reportApplication).getReportsByUser(userId);
    }

    @Test
    @DisplayName("사용자별 신고 목록 조회 - 리소스 없음 예외 테스트")
    void getReportsByUser_ResourceNotFound() {
        // given
        when(reportApplication.getReportsByUser(userId))
            .thenThrow(new ResourceNotFoundException("해당 사용자가 작성한 신고가 없습니다."));

        // when & then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> reportQueryResolver.getReportsByUser(parent, userId));
            
        assertEquals("해당 사용자가 작성한 신고가 없습니다.", exception.getMessage());
        verify(reportApplication).getReportsByUser(userId);
    }

    @Test
    @DisplayName("대상별 신고 목록 조회 성공 테스트")
    void getReportsByTarget_Success() {
        // given
        when(reportApplication.getReportsByTarget(targetId)).thenReturn(reportResponseList);

        // when
        List<ReportResponse> result = reportQueryResolver.getReportsByTarget(parent, targetId);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());
        assertEquals(targetId, result.get(0).getTargetId());
        assertEquals("테스트 신고 사유", result.get(0).getReason());
        verify(reportApplication).getReportsByTarget(targetId);
    }

    @Test
    @DisplayName("대상별 신고 목록 조회 - 비즈니스 예외 테스트")
    void getReportsByTarget_BusinessException() {
        // given
        when(reportApplication.getReportsByTarget(targetId))
            .thenThrow(new BusinessException("신고 대상 목록 조회 중 오류가 발생했습니다."));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> reportQueryResolver.getReportsByTarget(parent, targetId));
            
        assertEquals("신고 대상 목록 조회 중 오류가 발생했습니다.", exception.getMessage());
        verify(reportApplication).getReportsByTarget(targetId);
    }

    @Test
    @DisplayName("모든 신고 목록 조회 성공 테스트")
    void getAllReports_Success() {
        // given
        when(reportApplication.getAllReports()).thenReturn(reportResponseList);

        // when
        List<ReportResponse> result = reportQueryResolver.getAllReports(parent);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());
        assertEquals(targetId, result.get(0).getTargetId());
        assertEquals("테스트 신고 사유", result.get(0).getReason());
        verify(reportApplication).getAllReports();
    }
}
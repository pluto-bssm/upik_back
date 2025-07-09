package pluto.upik.domain.report.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pluto.upik.domain.guide.repository.GuideRepository;
import pluto.upik.domain.report.data.DTO.ReportResponse;
import pluto.upik.domain.report.data.model.Report;
import pluto.upik.domain.report.repository.ReportRepository;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ReportService 클래스에 대한 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private GuideRepository guideRepository;

    @InjectMocks
    private ReportService reportService;

    private UUID userId;
    private UUID targetId;
    private Report testReport;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        targetId = UUID.randomUUID();
        testReport = Report.builder()
                .userId(userId)
                .targetId(targetId)
                .reason("테스트 신고 사유")
                .createdAt(LocalDate.now())
                .build();
    }

    @Test
    @DisplayName("신고 삭제 성공 테스트")
    void deleteReport_Success() {
        // given
        when(reportRepository.existsByUserIdAndTargetId(userId, targetId)).thenReturn(true);
        doNothing().when(reportRepository).deleteByUserIdAndTargetId(userId, targetId);
        doNothing().when(guideRepository).decrementRevoteCount(targetId);

        // when
        assertDoesNotThrow(() -> reportService.deleteReport(userId, targetId));

        // then
        verify(reportRepository).existsByUserIdAndTargetId(userId, targetId);
        verify(reportRepository).deleteByUserIdAndTargetId(userId, targetId);
        verify(guideRepository).decrementRevoteCount(targetId);
    }

    @Test
    @DisplayName("존재하지 않는 신고 삭제 시 예외 발생 테스트")
    void deleteReport_NotFound() {
        // given
        when(reportRepository.existsByUserIdAndTargetId(userId, targetId)).thenReturn(false);

        // when & then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> reportService.deleteReport(userId, targetId));
        
        assertEquals("해당 신고가 존재하지 않습니다.", exception.getMessage());
        verify(reportRepository).existsByUserIdAndTargetId(userId, targetId);
        verify(reportRepository, never()).deleteByUserIdAndTargetId(any(), any());
        verify(guideRepository, never()).decrementRevoteCount(any());
    }

    @Test
    @DisplayName("가이드 revote 카운트 감소 중 오류 발생 테스트")
    void deleteReport_GuideUpdateError() {
        // given
        when(reportRepository.existsByUserIdAndTargetId(userId, targetId)).thenReturn(true);
        doNothing().when(reportRepository).deleteByUserIdAndTargetId(userId, targetId);
        doThrow(new RuntimeException("DB 오류")).when(guideRepository).decrementRevoteCount(targetId);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> reportService.deleteReport(userId, targetId));
            
        assertEquals("가이드 revote 카운트 감소 중 오류가 발생했습니다.", exception.getMessage());
        verify(reportRepository).existsByUserIdAndTargetId(userId, targetId);
        verify(reportRepository).deleteByUserIdAndTargetId(userId, targetId);
        verify(guideRepository).decrementRevoteCount(targetId);
    }

    @Test
    @DisplayName("사용자별 신고 목록 조회 성공 테스트")
    void getReportsByUser_Success() {
        // given
        List<Report> reportList = Arrays.asList(testReport);
        when(reportRepository.findByUserId(userId)).thenReturn(reportList);

        // when
        List<ReportResponse> result = reportService.getReportsByUser(userId);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());
        assertEquals(targetId, result.get(0).getTargetId());
        assertEquals("테스트 신고 사유", result.get(0).getReason());
        verify(reportRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("사용자별 신고 목록 조회 - 결과 없음 테스트")
    void getReportsByUser_NoResults() {
        // given
        when(reportRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        // when & then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> reportService.getReportsByUser(userId));
            
        assertEquals("해당 사용자가 작성한 신고가 없습니다.", exception.getMessage());
        verify(reportRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("대상별 신고 목록 조회 성공 테스트")
    void getReportsByTarget_Success() {
        // given
        List<Report> reportList = Arrays.asList(testReport);
        when(reportRepository.findByTargetId(targetId)).thenReturn(reportList);

        // when
        List<ReportResponse> result = reportService.getReportsByTarget(targetId);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());
        assertEquals(targetId, result.get(0).getTargetId());
        assertEquals("테스트 신고 사유", result.get(0).getReason());
        verify(reportRepository).findByTargetId(targetId);
    }

    @Test
    @DisplayName("대상별 신고 목록 조회 - 결과 없음 테스트")
    void getReportsByTarget_NoResults() {
        // given
        when(reportRepository.findByTargetId(targetId)).thenReturn(Collections.emptyList());

        // when & then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> reportService.getReportsByTarget(targetId));
            
        assertEquals("해당 대상에 대한 신고가 없습니다.", exception.getMessage());
        verify(reportRepository).findByTargetId(targetId);
    }

    @Test
    @DisplayName("모든 신고 목록 조회 성공 테스트")
    void getAllReports_Success() {
        // given
        List<Report> reportList = Arrays.asList(testReport);
        when(reportRepository.findAll()).thenReturn(reportList);

        // when
        List<ReportResponse> result = reportService.getAllReports();

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());
        assertEquals(targetId, result.get(0).getTargetId());
        assertEquals("테스트 신고 사유", result.get(0).getReason());
        verify(reportRepository).findAll();
    }

    @Test
    @DisplayName("모든 신고 목록 조회 - 결과 없음 테스트")
    void getAllReports_NoResults() {
        // given
        when(reportRepository.findAll()).thenReturn(Collections.emptyList());

        // when & then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> reportService.getAllReports());
            
        assertEquals("신고 내역이 존재하지 않습니다.", exception.getMessage());
        verify(reportRepository).findAll();
    }
}
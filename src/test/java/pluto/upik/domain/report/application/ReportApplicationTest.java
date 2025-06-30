package pluto.upik.domain.report.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pluto.upik.domain.report.data.DTO.*;
import pluto.upik.domain.report.service.ReportServiceInterface;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportApplicationTest {

    @Mock
    private ReportServiceInterface reportService;

    @InjectMocks
    private ReportApplication reportApplication;

    private UUID testUserId;
    private UUID testTargetId;
    private UUID testGuideId;
    private UUID testQuestionId;
    private UUID testNewQuestionId;
    private ReportResponse testReportResponse;
    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testTargetId = UUID.randomUUID();
        testGuideId = UUID.randomUUID();
        testQuestionId = UUID.randomUUID();
        testNewQuestionId = UUID.randomUUID();
        testReportResponse = new ReportResponse(
                testUserId,
                testTargetId,
                "Test reason",
                LocalDate.now()
        );
    }

    @Test
    @DisplayName("신고 거부 테스트 - 성공")
    void rejectReportSuccess() {
        // Given
        doNothing().when(reportService).deleteReport(testUserId, testTargetId);

        // When
        String result = reportApplication.rejectReport(testUserId, testTargetId);

        // Then
        assertEquals("거부 성공", result);
        verify(reportService).deleteReport(testUserId, testTargetId);
    }

    @Test
    @DisplayName("가이드 신고 수락 테스트 - 성공")
    void acceptGuideReportSuccess() {
        // Given
        AcceptGuideReportRequest request = AcceptGuideReportRequest.builder()
                .userId(testUserId)
                .guideId(testGuideId)
                .build();

        AcceptGuideReportResponse expectedResponse = AcceptGuideReportResponse.builder()
                .message("가이드 신고가 수락되었습니다. 질문이 다시 열렸습니다.")
                .newQuestionId(testNewQuestionId)
                .success(true)
                .build();
        when(reportService.acceptGuideReport(request)).thenReturn(expectedResponse);

        // When
        AcceptGuideReportResponse result = reportApplication.acceptGuideReport(request);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(testNewQuestionId, result.getNewQuestionId());
        verify(reportService).acceptGuideReport(request);
    }

    @Test
    @DisplayName("질문 신고 테스트 - 성공")
    void reportQuestionSuccess() {
        // Given
        QuestionReportRequest request = QuestionReportRequest.builder()
                .userId(testUserId)
                .questionId(testQuestionId)
                .reason("Test reason")
                .build();

        QuestionReportResponse expectedResponse = QuestionReportResponse.builder()
                .message("질문 신고가 접수되었습니다.")
                .success(true)
                .build();
        when(reportService.reportQuestion(request)).thenReturn(expectedResponse);

        // When
        QuestionReportResponse result = reportApplication.reportQuestion(request);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(reportService).reportQuestion(request);
    }

    @Test
    @DisplayName("질문 신고 거부 테스트 - 성공")
    void rejectQuestionReportSuccess() {
        // Given
        RejectQuestionReportRequest request = RejectQuestionReportRequest.builder()
                .userId(testUserId)
                .questionId(testQuestionId)
                .build();

        QuestionReportResponse expectedResponse = QuestionReportResponse.builder()
                .message("질문 신고가 거부되었습니다.")
                .success(true)
                .build();
        when(reportService.rejectQuestionReport(request)).thenReturn(expectedResponse);

        // When
        QuestionReportResponse result = reportApplication.rejectQuestionReport(request);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(reportService).rejectQuestionReport(request);
    }

    @Test
    @DisplayName("질문 신고 수락 테스트 - 성공")
    void acceptQuestionReportSuccess() {
        // Given
        AcceptQuestionReportRequest request = AcceptQuestionReportRequest.builder()
                .userId(testUserId)
                .questionId(testQuestionId)
                .build();

        QuestionReportResponse expectedResponse = QuestionReportResponse.builder()
                .message("질문 신고가 수락되었습니다. 질문이 삭제되었습니다.")
                .success(true)
                .build();

        when(reportService.acceptQuestionReport(request)).thenReturn(expectedResponse);

        // When
        QuestionReportResponse result = reportApplication.acceptQuestionReport(request);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(reportService).acceptQuestionReport(request);
}

    @Test
    @DisplayName("사용자별 신고 목록 조회 테스트 - 성공")
    void getReportsByUserSuccess() {
        // Given
        List<ReportResponse> expectedReports = Arrays.asList(testReportResponse);
        when(reportService.getReportsByUser(testUserId)).thenReturn(expectedReports);

        // When
        List<ReportResponse> result = reportApplication.getReportsByUser(testUserId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUserId, result.get(0).getUserId());
        verify(reportService).getReportsByUser(testUserId);
    }
}

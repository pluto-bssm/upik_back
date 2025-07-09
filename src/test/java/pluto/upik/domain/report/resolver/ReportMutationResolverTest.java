package pluto.upik.domain.report.resolver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pluto.upik.domain.report.application.ReportApplicationInterface;
import pluto.upik.domain.report.data.DTO.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportMutationResolverTest {

    @Mock
    private ReportApplicationInterface reportApplication;

    @InjectMocks
    private ReportMutationResolver reportMutationResolver;

    private UUID testUserId;
    private UUID testTargetId;
    private UUID testGuideId;
    private UUID testQuestionId;
    private UUID testNewQuestionId;
    private String testUserIdString;
    private String testTargetIdString;
    private String testGuideIdString;
    private String testQuestionIdString;
    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testTargetId = UUID.randomUUID();
        testGuideId = UUID.randomUUID();
        testQuestionId = UUID.randomUUID();
        testNewQuestionId = UUID.randomUUID();
        testUserIdString = testUserId.toString();
        testTargetIdString = testTargetId.toString();
        testGuideIdString = testGuideId.toString();
        testQuestionIdString = testQuestionId.toString();
    }

    @Test
    @DisplayName("신고 거부 GraphQL 리졸버 테스트 - 성공")
    void rejectReportSuccess() {
        // Given
        when(reportApplication.rejectReport(testUserId, testTargetId)).thenReturn("거부 성공");

        // When
        RejectReportPayload result = reportMutationResolver.rejectReport(testUserIdString, testTargetIdString);

        // Then
        assertNotNull(result);
        assertEquals("거부 성공", result.getMessage());
        verify(reportApplication).rejectReport(testUserId, testTargetId);
    }

    @Test
    @DisplayName("가이드 신고 수락 GraphQL 리졸버 테스트 - 성공")
    void acceptGuideReportSuccess() {
        // Given
        AcceptGuideReportResponse expectedResponse = AcceptGuideReportResponse.builder()
                .message("가이드 신고가 수락되었습니다. 질문이 다시 열렸습니다.")
                .newQuestionId(testNewQuestionId)
                .success(true)
                .build();

        when(reportApplication.acceptGuideReport(any(AcceptGuideReportRequest.class))).thenReturn(expectedResponse);
        // When
        AcceptGuideReportResponse result = reportMutationResolver.acceptGuideReport(testUserIdString, testGuideIdString);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(testNewQuestionId, result.getNewQuestionId());
        verify(reportApplication).acceptGuideReport(any(AcceptGuideReportRequest.class));
    }

    @Test
    @DisplayName("질문 신고 GraphQL 리졸버 테스트 - 성공")
    void reportQuestionSuccess() {
        // Given
        String reason = "Test reason";
        QuestionReportResponse expectedResponse = QuestionReportResponse.builder()
                .message("질문 신고가 접수되었습니다.")
                .success(true)
                .build();

        when(reportApplication.reportQuestion(any(QuestionReportRequest.class))).thenReturn(expectedResponse);

        // When
        QuestionReportResponse result = reportMutationResolver.reportQuestion(testUserIdString, testQuestionIdString, reason);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(reportApplication).reportQuestion(any(QuestionReportRequest.class));
    }
    @Test
    @DisplayName("질문 신고 거부 GraphQL 리졸버 테스트 - 성공")
    void rejectQuestionReportSuccess() {
        // Given
        QuestionReportResponse expectedResponse = QuestionReportResponse.builder()
                .message("질문 신고가 거부되었습니다.")
                .success(true)
                .build();

        when(reportApplication.rejectQuestionReport(any(RejectQuestionReportRequest.class))).thenReturn(expectedResponse);

        // When
        QuestionReportResponse result = reportMutationResolver.rejectQuestionReport(testUserIdString, testQuestionIdString);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(reportApplication).rejectQuestionReport(any(RejectQuestionReportRequest.class));
    }

    @Test
    @DisplayName("질문 신고 수락 GraphQL 리졸버 테스트 - 성공")
    void acceptQuestionReportSuccess() {
        // Given
        QuestionReportResponse expectedResponse = QuestionReportResponse.builder()
                .message("질문 신고가 수락되었습니다. 질문이 삭제되었습니다.")
                .success(true)
                .build();

        when(reportApplication.acceptQuestionReport(any(AcceptQuestionReportRequest.class))).thenReturn(expectedResponse);

        // When
        QuestionReportResponse result = reportMutationResolver.acceptQuestionReport(testUserIdString, testQuestionIdString);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(reportApplication).acceptQuestionReport(any(AcceptQuestionReportRequest.class));
    }

    @Test
    @DisplayName("잘못된 UUID 형식 테스트")
    void invalidUuidFormat() {
        // Given
        String invalidUuid = "invalid-uuid";
        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> reportMutationResolver.rejectReport(invalidUuid, testTargetIdString));

        assertThrows(IllegalArgumentException.class,
                () -> reportMutationResolver.acceptGuideReport(invalidUuid, testGuideIdString));

        assertThrows(IllegalArgumentException.class,
                () -> reportMutationResolver.reportQuestion(invalidUuid, testQuestionIdString, "reason"));

        assertThrows(IllegalArgumentException.class,
                () -> reportMutationResolver.rejectQuestionReport(invalidUuid, testQuestionIdString));

        assertThrows(IllegalArgumentException.class,
                () -> reportMutationResolver.acceptQuestionReport(invalidUuid, testQuestionIdString));
    }
}
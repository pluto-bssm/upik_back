package pluto.upik.domain.report.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 질문 신고 요청을 위한 DTO 클래스
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionReportRequest {
    /**
     * 신고하는 사용자 ID
     */
    private UUID userId;
    
    /**
     * 신고 대상 질문 ID
     */
    private UUID questionId;
    
    /**
     * 신고 사유
     */
    private String reason;
}
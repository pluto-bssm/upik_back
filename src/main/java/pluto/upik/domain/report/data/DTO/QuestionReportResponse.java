package pluto.upik.domain.report.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 질문 신고 결과를 위한 DTO 클래스
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionReportResponse {
    /**
     * 처리 결과 메시지
     */
    private String message;
    
    /**
     * 처리 성공 여부
     */
    private boolean success;
}
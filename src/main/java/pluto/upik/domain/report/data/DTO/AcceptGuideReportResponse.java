package pluto.upik.domain.report.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 가이드 신고 수락 결과를 위한 DTO 클래스
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcceptGuideReportResponse {
    /**
     * 처리 결과 메시지
     */
    private String message;
    
    /**
     * 새로 생성된 질문 ID (있는 경우)
     */
    private UUID newQuestionId;
    
    /**
     * 처리 성공 여부
     */
    private boolean success;
}
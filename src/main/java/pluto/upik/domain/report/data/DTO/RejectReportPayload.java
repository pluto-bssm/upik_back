package pluto.upik.domain.report.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 신고 거부 처리 결과를 클라이언트에 전달하기 위한 DTO 클래스
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RejectReportPayload {
    
    /**
     * 신고 거부 처리 결과 메시지
     */
    private String message;
}
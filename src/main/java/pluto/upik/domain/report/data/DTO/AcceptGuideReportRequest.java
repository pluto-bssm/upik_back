package pluto.upik.domain.report.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 가이드 신고 수락 요청을 위한 DTO 클래스
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcceptGuideReportRequest {
    /**
     * 신고한 사용자 ID
     */
    private UUID userId;
    
    /**
     * 신고 대상 가이드 ID
     */
    private UUID guideId;
}
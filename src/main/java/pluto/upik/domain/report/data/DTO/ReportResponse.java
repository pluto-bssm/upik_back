package pluto.upik.domain.report.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 신고 정보를 클라이언트에 전달하기 위한 DTO 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResponse {
    /**
     * 신고한 사용자의 ID
     */
    private UUID userId;
    
    /**
     * 신고 대상의 ID
     */
    private UUID targetId;
    
    /**
     * 신고 사유
     */
    private String reason;
    
    /**
     * 신고 생성 일자
     */
    private LocalDate createdAt;
    
    /**
     * DTO의 문자열 표현을 반환하는 메서드
     * 민감한 정보는 마스킹 처리합니다.
     *
     * @return DTO의 문자열 표현
     */
    @Override
    public String toString() {
        return "ReportResponse{" +
                "userId=" + userId +
                ", targetId=" + targetId +
                ", reason='" + (reason != null ? reason.substring(0, Math.min(10, reason.length())) + "..." : null) + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
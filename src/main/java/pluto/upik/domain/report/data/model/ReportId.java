package pluto.upik.domain.report.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

/**
 * 신고 엔티티의 복합 키 클래스
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class ReportId implements Serializable {
    
    /**
     * 신고자 ID
     */
    private UUID userId;
    
    /**
     * 신고 대상 ID
     */
    private UUID targetId;
}
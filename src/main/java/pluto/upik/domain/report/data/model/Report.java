package pluto.upik.domain.report.data.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 신고 정보를 저장하는 엔티티 클래스
 * 사용자가 특정 대상(가이드 등)에 대해 신고한 내용을 관리합니다.
 */
@Entity
@Table(name = "report")
@IdClass(ReportId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Report {

    /**
     * 신고한 사용자의 ID (복합 기본키의 일부)
     */
    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /**
     * 신고 대상의 ID (복합 기본키의 일부)
     */
    @Id
    @Column(name = "target_id", nullable = false)
    private UUID targetId;

    /**
     * 신고 사유
     */
    @Column(name = "reason", length = 500)
    private String reason;

    /**
     * 신고 생성 일자
     */
    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;
}
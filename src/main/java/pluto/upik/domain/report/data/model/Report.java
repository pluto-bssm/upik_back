package pluto.upik.domain.report.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 신고 엔티티
 * 사용자가 제출한 신고 정보를 저장합니다.
 */
@Entity
@Table(name = "report")
@IdClass(ReportId.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Report {

    /**
     * 신고자 ID (복합 키)
     */
    @Id
    @Column(columnDefinition = "uuid")
    private UUID userId;

    /**
     * 신고 대상 ID (복합 키)
     */
    @Id
    @Column(columnDefinition = "uuid")
    private UUID targetId;

    /**
     * 신고 사유
     */
    @Column(columnDefinition = "TEXT")
    private String reason;

    /**
     * 생성 일시
     */
    @Column
    private LocalDate createdAt;
}
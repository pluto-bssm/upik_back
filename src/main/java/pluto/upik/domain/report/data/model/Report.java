package pluto.upik.domain.report.data.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "report")
@IdClass(ReportId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Id
    @Column(name = "target_id")
    private UUID targetId;

    private String reason;

    @Column(name = "created_at")
    private LocalDate createdAt;
}

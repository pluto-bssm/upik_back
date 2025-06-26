package pluto.upik.domain.report.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class ReportId implements Serializable {
    private UUID userId;
    private UUID targetId;

    // 기본 생성자
    public ReportId() {}

    public ReportId(UUID userId, UUID targetId) {
        this.userId = userId;
        this.targetId = targetId;
    }

    // equals() & hashCode() 필수
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReportId)) return false;
        ReportId that = (ReportId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(targetId, that.targetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, targetId);
    }
}
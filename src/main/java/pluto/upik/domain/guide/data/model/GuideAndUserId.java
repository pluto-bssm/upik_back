package pluto.upik.domain.guide.data.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class GuideAndUserId implements Serializable {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "guide_id")
    private UUID guideId;

    // equals, hashCode는 @EqualsAndHashCode로 자동 생성됨
}

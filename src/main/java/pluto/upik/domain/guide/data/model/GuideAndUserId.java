package pluto.upik.domain.guide.data.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

/**
 * 가이드와 사용자 간의 관계를 나타내는 복합 키 클래스
 * 좋아요 등의 사용자-가이드 간 관계를 식별하는 데 사용됩니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Embeddable
public class GuideAndUserId implements Serializable {

    /**
     * 사용자 ID
     */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /**
     * 가이드 ID
     */
    @Column(name = "guide_id", nullable = false)
    private UUID guideId;
}
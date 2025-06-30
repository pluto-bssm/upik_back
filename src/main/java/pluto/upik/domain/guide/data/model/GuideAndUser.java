package pluto.upik.domain.guide.data.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 가이드와 사용자 간의 관계를 나타내는 엔티티 클래스
 * 사용자가 가이드에 좋아요를 누른 관계를 저장합니다.
 */
@Entity
@Table(name = "guide_and_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class GuideAndUser {

    /**
     * 복합 키 (사용자 ID + 가이드 ID)
     */
    @EmbeddedId
    private GuideAndUserId id;

    // 만약 연관관계를 추가하고 싶으면 아래처럼 할 수 있지만,
    // 단순히 id만 관리할 경우 생략 가능
    /*
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("guideId")
    @JoinColumn(name = "guide_id")
    private Guide guide;
    */
}
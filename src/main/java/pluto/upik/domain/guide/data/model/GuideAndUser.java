package pluto.upik.domain.guide.data.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "guide_and_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuideAndUser {

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

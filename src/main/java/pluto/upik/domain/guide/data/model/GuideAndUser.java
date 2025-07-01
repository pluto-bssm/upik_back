package pluto.upik.domain.guide.data.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 가이드와 사용자 간의 관계를 나타내는 엔티티 클래스
 * 사용자가 가이드에 좋아요를 누른 관계를 저장합니다.
 */
@Entity
@Table(name = "guide_and_user")
@Getter
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
}

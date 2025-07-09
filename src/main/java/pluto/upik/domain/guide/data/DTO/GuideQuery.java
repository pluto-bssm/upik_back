package pluto.upik.domain.guide.data.DTO;

import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * GraphQL 가이드 쿼리의 루트 객체를 나타내는 DTO 클래스
 * 이 클래스는 GraphQL 스키마의 GuideQuery 타입에 매핑됩니다.
 */
@NoArgsConstructor
@ToString
public class GuideQuery {
    // 이 클래스는 GraphQL 스키마의 GuideQuery 타입에 매핑되는 빈 컨테이너 역할을 합니다.
    // 실제 쿼리 처리는 GuideQueryResolver에서 이루어집니다.
}
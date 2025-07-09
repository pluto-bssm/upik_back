package pluto.upik.domain.guide.data.DTO;

import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * GraphQL 가이드 뮤테이션의 루트 객체를 나타내는 DTO 클래스
 * 이 클래스는 GraphQL 스키마의 GuideMutation 타입에 매핑됩니다.
 */
@NoArgsConstructor
@ToString
public class GuideMutation {
    // 이 클래스는 GraphQL 스키마의 GuideMutation 타입에 매핑되는 빈 컨테이너 역할을 합니다.
    // 실제 뮤테이션 처리는 GuideMutationResolver에서 이루어집니다.
}
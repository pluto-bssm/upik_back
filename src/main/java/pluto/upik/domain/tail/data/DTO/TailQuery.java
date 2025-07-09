package pluto.upik.domain.tail.data.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 테일 쿼리 DTO
 * GraphQL 테일 쿼리의 루트 객체입니다.
 */
@Getter
@NoArgsConstructor
public class TailQuery {
    // 이 클래스는 GraphQL 스키마의 TailQuery 타입에 대응됩니다.
    // 실제 구현은 TailQueryResolver에서 이루어집니다.
}
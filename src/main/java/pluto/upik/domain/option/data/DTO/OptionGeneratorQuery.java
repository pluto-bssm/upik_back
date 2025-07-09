package pluto.upik.domain.option.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 선택지 생성 쿼리 DTO
 * GraphQL 쿼리 타입을 위한 클래스입니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionGeneratorQuery {
    // GraphQL 스키마의 OptionGeneratorQuery 타입에 대응하는 클래스
    // 필요한 경우 여기에 필드를 추가할 수 있습니다.
    private String id = "optionGenerator"; // 식별용 더미 필드
}
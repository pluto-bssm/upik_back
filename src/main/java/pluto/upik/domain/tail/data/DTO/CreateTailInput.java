package pluto.upik.domain.tail.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

/**
 * 테일 생성 입력 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CreateTailInput {
    
    /**
     * 테일이 속할 투표 ID
     */
    private String voteId;

    /**
     * 테일 질문
     */
    private String question;
}

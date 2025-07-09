package pluto.upik.domain.tail.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

/**
 * 테일 응답 생성 입력 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CreateTailResponseInput {
    
    /**
     * 응답할 테일 ID
     */
    private String tailId;

    /**
     * 응답하는 사용자 ID
     */
    private String userId;

    /**
     * 응답 내용
     */
    private String answer;

    /**
     * tailId를 UUID로 변환
     *
     * @return UUID 형태의 tailId
     */
    public UUID getTailIdAsUUID() {
        return tailId != null ? UUID.fromString(tailId) : null;
}

    /**
     * userId를 UUID로 변환
     *
     * @return UUID 형태의 userId
     */
    public UUID getUserIdAsUUID() {
        return userId != null ? UUID.fromString(userId) : null;
    }
}

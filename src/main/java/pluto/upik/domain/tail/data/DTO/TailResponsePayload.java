package pluto.upik.domain.tail.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pluto.upik.domain.tail.data.model.TailResponse;

import java.util.UUID;

/**
 * 테일 응답 정보 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TailResponsePayload {
    
    /**
     * 테일 응답 ID
     */
    private UUID id;
    
    /**
     * 응답한 테일 ID
     */
    private UUID tailId;
    
    /**
     * 응답한 사용자 ID
     */
    private UUID userId;
    
    /**
     * 응답 내용
     */
    private String answer;
    
    /**
     * 테일 응답 엔티티로부터 TailResponsePayload 생성
     * 
     * @param tailResponse 테일 응답 엔티티
     * @return TailResponsePayload 객체
     */
    public static TailResponsePayload fromEntity(TailResponse tailResponse) {
        return TailResponsePayload.builder()
                .id(tailResponse.getId())
                .tailId(tailResponse.getTail().getId())
                .userId(tailResponse.getUser().getId())
                .answer(tailResponse.getAnswer())
                .build();
    }
}
package pluto.upik.domain.tail.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pluto.upik.domain.tail.data.model.Tail;

import java.util.UUID;

/**
 * 테일 정보 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TailPayload {
    
    /**
     * 테일 ID
     */
    private UUID id;
    
    /**
     * 테일이 속한 투표 ID
     */
    private UUID voteId;
    
    /**
     * 테일 질문
     */
    private String question;
    
    /**
     * 테일 엔티티로부터 TailPayload 생성
     * 
     * @param tail 테일 엔티티
     * @return TailPayload 객체
     */
    public static TailPayload fromEntity(Tail tail) {
        return TailPayload.builder()
                .id(tail.getId())
                .voteId(tail.getVote().getId())
                .question(tail.getQuestion())
                .build();
    }
}
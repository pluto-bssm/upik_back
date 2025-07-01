package pluto.upik.domain.voteResponse.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pluto.upik.domain.voteResponse.data.model.VoteResponse;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteResponsePayload {
    private UUID id;
    private UUID userId;
    private UUID voteId;
    private UUID optionId;
    private String optionContent;
    private String voteTitle;
    private LocalDate createdAt;

    // 정적 팩토리 메서드
    public static VoteResponsePayload fromEntity(VoteResponse voteResponse) {
        return VoteResponsePayload.builder()
                .id(voteResponse.getId())
                .userId(voteResponse.getUser().getId())
                .voteId(voteResponse.getVote().getId())
                .optionId(voteResponse.getSelectedOption().getId())
                .optionContent(voteResponse.getSelectedOption().getContent())
                .voteTitle(voteResponse.getVote().getQuestion())
                .createdAt(voteResponse.getCreatedAt())
                .build();
    }
}

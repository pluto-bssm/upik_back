package pluto.upik.domain.voteResponse.data.DTO;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateVoteResponseInput {
    private UUID voteId;    // 어떤 투표에 참여할지
    private UUID optionId;  // 어떤 선택지를 선택할지
}
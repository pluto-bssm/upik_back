package pluto.upik.domain.voteResponse.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pluto.upik.domain.voteResponse.data.DTO.CreateVoteResponseInput;
import pluto.upik.domain.voteResponse.data.DTO.VoteResponsePayload;
import pluto.upik.domain.voteResponse.service.VoteResponseService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VoteResponseApplication {

    private final VoteResponseService voteResponseService;
    // VoteResponseApplication.java에 추가할 메서드
    public Boolean hasUserVoted(UUID userId, UUID voteId) {
        return voteResponseService.hasUserVoted(userId, voteId);
    }

    public VoteResponsePayload createVoteResponse(CreateVoteResponseInput input, UUID userId) {
        return voteResponseService.createVoteResponse(input, userId);
    }

    public Long getVoteResponseCount(UUID voteId) {
        return voteResponseService.getVoteResponseCount(voteId);
    }

    public Long getOptionResponseCount(UUID optionId) {
        return voteResponseService.getOptionResponseCount(optionId);
    }
}

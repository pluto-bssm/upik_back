package pluto.upik.domain.voteResponse.resolver;


import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.voteResponse.application.VoteResponseApplication;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class VoteResponseQueryResolver {

    private final VoteResponseApplication voteResponseApplication;

    @SchemaMapping(typeName = "VoteResponseQuery", field = "getVoteResponseCount")
    public Integer getVoteResponseCount(@Argument UUID voteId) {
        return voteResponseApplication.getVoteResponseCount(voteId).intValue();
    }

    @SchemaMapping(typeName = "VoteResponseQuery", field = "getOptionResponseCount")
    public Integer getOptionResponseCount(@Argument UUID optionId) {
        return voteResponseApplication.getOptionResponseCount(optionId).intValue();
    }

    @SchemaMapping(typeName = "VoteResponseQuery", field = "hasUserVoted")
    public Boolean hasUserVoted(@Argument UUID voteId) {
        // 목 데이터로 더미 사용자 ID 사용
        UUID dummyUserId = UUID.fromString("e49207e8-471a-11f0-937c-42010a800003");

        return voteResponseApplication.hasUserVoted(dummyUserId, voteId);
    }
}

package pluto.upik.domain.vote.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.vote.application.VoteApplication;
import pluto.upik.domain.vote.data.DTO.CreateVoteInput;
import pluto.upik.domain.vote.data.DTO.VotePayload;

@Controller
@RequiredArgsConstructor
public class VoteMutationResolver {

    private final VoteApplication voteApplication;

    @SchemaMapping(typeName = "VoteMutation", field = "createVote")
    public VotePayload createVote(@Argument CreateVoteInput input) {
        return voteApplication.createVote(input);
    }
}
package pluto.upik.domain.vote.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.vote.data.DTO.VoteDetailPayload;
import pluto.upik.domain.vote.data.DTO.VotePayload;
import pluto.upik.domain.vote.service.VoteService;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class VoteQueryResolver {

    private final VoteService voteService;

    @SchemaMapping(typeName = "VoteQuery", field = "getAllVotes")
    public List<VotePayload> getAllVotes() {
        return voteService.getAllVotes();
    }

    @SchemaMapping(typeName = "VoteQuery", field = "getVoteById")
    public VoteDetailPayload getVoteById(@Argument String id) {
        return voteService.getVoteById(UUID.fromString(id));
    }

    @SchemaMapping(typeName = "VoteQuery", field = "getMostPopularOpenVote")
    public VotePayload getMostPopularOpenVote() {
        return voteService.getMostPopularOpenVote();
    }

    @SchemaMapping(typeName = "VoteQuery", field = "getLeastPopularOpenVote")
    public VotePayload getLeastPopularOpenVote() {
        return voteService.getLeastPopularOpenVote();
    }
}
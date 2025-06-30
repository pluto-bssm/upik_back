package pluto.upik.domain.vote.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.vote.data.DTO.VoteMutation;

@Controller
@RequiredArgsConstructor
public class VoteRootMutationResolver {

    private final VoteMutationResolver voteMutationResolver;

    @SchemaMapping(typeName = "Mutation", field = "vote")
    public VoteMutation vote() {
        return new VoteMutation();
    }
}
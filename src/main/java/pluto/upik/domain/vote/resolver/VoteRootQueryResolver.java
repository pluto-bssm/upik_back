package pluto.upik.domain.vote.resolver;

import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.vote.data.DTO.VoteQuery;

@Controller
public class VoteRootQueryResolver {

    @SchemaMapping(typeName = "Query", field = "vote")
    public VoteQuery vote() {
        return new VoteQuery();
    }
}
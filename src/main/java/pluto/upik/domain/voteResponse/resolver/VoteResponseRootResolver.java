package pluto.upik.domain.voteResponse.resolver;

import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class VoteResponseRootResolver {

    @SchemaMapping(typeName = "Query", field = "voteResponse")
    public Object voteResponseQuery() {
        return new Object(); // VoteResponseQuery 타입의 루트 객체 반환
    }

    @SchemaMapping(typeName = "Mutation", field = "voteResponse")
    public Object voteResponseMutation() {
        return new Object(); // VoteResponseMutation 타입의 루트 객체 반환
    }
}

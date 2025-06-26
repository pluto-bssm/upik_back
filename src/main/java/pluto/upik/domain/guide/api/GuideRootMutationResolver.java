package pluto.upik.domain.guide.api;

import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.guide.data.DTO.GuideMutation;

@Controller
public class GuideRootMutationResolver {

    @MutationMapping
    public GuideMutation guide() {
        return new GuideMutation();
    }
}

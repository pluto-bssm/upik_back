package pluto.upik.domain.guide.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.guide.data.DTO.GuideMutation;
import pluto.upik.domain.guide.service.GuideInteractionService;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class GuideMutationResolver {

    private final GuideInteractionService guideInteractionService;

    @SchemaMapping(typeName = "GuideMutation", field = "incrementGuideLike")
    public boolean incrementGuideLike(GuideMutation parent, @Argument String id) {
        UUID dummyUserId = UUID.fromString("e49207e8-471a-11f0-937c-42010a800003");
        return guideInteractionService.toggleLikeGuide(dummyUserId,UUID.fromString(id));
    }

    @SchemaMapping(typeName = "GuideMutation", field = "incrementGuideRevote")
    public boolean incrementGuideRevote(
            GuideMutation parent,
            @Argument String id,
            @Argument String reason) {
        // 임시 userId 예시 (로그인된 유저 정보에서 꺼내야 함)
        UUID dummyUserId = UUID.fromString("e49207e8-471a-11f0-937c-42010a800003");

        return guideInteractionService.toggleReportAndRevote(UUID.fromString(id), dummyUserId, reason);
    }
}
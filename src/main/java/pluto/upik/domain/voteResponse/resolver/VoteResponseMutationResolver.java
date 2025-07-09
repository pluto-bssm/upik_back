package pluto.upik.domain.voteResponse.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.voteResponse.application.VoteResponseApplication;
import pluto.upik.domain.voteResponse.data.DTO.CreateVoteResponseInput;
import pluto.upik.domain.voteResponse.data.DTO.VoteResponsePayload;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class VoteResponseMutationResolver {

    private final VoteResponseApplication voteResponseApplication;

    @SchemaMapping(typeName = "VoteResponseMutation", field = "createVoteResponse")
    public VoteResponsePayload createVoteResponse(@Argument CreateVoteResponseInput input) {
        // 목 데이터로 더미 사용자 ID 사용
        UUID dummyUserId = UUID.fromString("e49207e8-471a-11f0-937c-42010a800003");

        return voteResponseApplication.createVoteResponse(input, dummyUserId);
    }

    private UUID extractUserIdFromAuthentication(Authentication authentication) {
        // JWT 토큰이나 다른 인증 방식에서 사용자 ID 추출
        // 실제 구현에 맞게 수정해야 합니다
        String userIdString = authentication.getName(); // 또는 다른 방식
        return UUID.fromString(userIdString);
    }
}

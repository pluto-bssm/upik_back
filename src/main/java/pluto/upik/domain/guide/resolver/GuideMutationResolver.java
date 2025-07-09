package pluto.upik.domain.guide.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.guide.data.DTO.GuideMutation;
import pluto.upik.domain.guide.service.GuideInteractionServiceInterface;

import java.util.UUID;

/**
 * 가이드 관련 GraphQL 뮤테이션 리졸버
 * 가이드 좋아요, 신고 등의 뮤테이션 요청을 처리합니다.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class GuideMutationResolver {

    private final GuideInteractionServiceInterface guideInteractionService;

    /**
     * 가이드 좋아요 증가/취소 뮤테이션을 처리합니다.
     *
     * @param parent GraphQL 부모 객체
     * @param id 가이드 ID 문자열
     * @return 좋아요가 추가되었으면 true, 취소되었으면 false
     */
    @SchemaMapping(typeName = "GuideMutation", field = "incrementGuideLike")
    public boolean incrementGuideLike(GuideMutation parent, @Argument String id) {
        log.info("GraphQL 뮤테이션 - 가이드 좋아요 토글 요청: id={}", id);
        
        try {
            // 임시 사용자 ID (실제로는 인증된 사용자 정보에서 가져와야 함)
            UUID dummyUserId = UUID.fromString("e49207e8-471a-11f0-937c-42010a800003");
            UUID guideId = UUID.fromString(id);
            
            boolean result = guideInteractionService.toggleLikeGuide(dummyUserId, guideId);
            log.info("GraphQL 뮤테이션 - 가이드 좋아요 토글 완료: id={}, 결과={}", id, result ? "추가됨" : "취소됨");
            
            return result;
        } catch (IllegalArgumentException e) {
            log.error("GraphQL 뮤테이션 - 가이드 좋아요 토글 실패: 잘못된 UUID 형식 - id={}", id, e);
            throw new IllegalArgumentException("잘못된 UUID 형식입니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("GraphQL 뮤테이션 - 가이드 좋아요 토글 실패: id={}", id, e);
            throw e;
        }
    }

    /**
     * 가이드 재투표 신고 증가/취소 뮤테이션을 처리합니다.
     *
     * @param parent GraphQL 부모 객체
     * @param id 가이드 ID 문자열
     * @param reason 신고 사유
     * @return 신고가 추가되었으면 true, 취소되었으면 false
     */
    @SchemaMapping(typeName = "GuideMutation", field = "incrementGuideRevote")
    public boolean incrementGuideRevote(
            GuideMutation parent,
            @Argument String id,
            @Argument String reason) {
        log.info("GraphQL 뮤테이션 - 가이드 재투표 신고 토글 요청: id={}, reason={}", id, reason);
        
        try {
            // 임시 사용자 ID (실제로는 인증된 사용자 정보에서 가져와야 함)
            UUID dummyUserId = UUID.fromString("e49207e8-471a-11f0-937c-42010a800003");
            UUID guideId = UUID.fromString(id);
            
            boolean result = guideInteractionService.toggleReportAndRevote(guideId, dummyUserId, reason);
            log.info("GraphQL 뮤테이션 - 가이드 재투표 신고 토글 완료: id={}, 결과={}", id, result ? "추가됨" : "취소됨");
            
            return result;
        } catch (IllegalArgumentException e) {
            log.error("GraphQL 뮤테이션 - 가이드 재투표 신고 토글 실패: 잘못된 UUID 형식 - id={}", id, e);
            throw new IllegalArgumentException("잘못된 UUID 형식입니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("GraphQL 뮤테이션 - 가이드 재투표 신고 토글 실패: id={}, reason={}", id, reason, e);
            throw e;
        }
    }
}
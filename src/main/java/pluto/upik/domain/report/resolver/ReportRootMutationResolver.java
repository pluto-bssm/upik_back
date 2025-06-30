package pluto.upik.domain.report.resolver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.report.data.DTO.ReportMutation;

/**
 * 신고 관련 GraphQL 뮤테이션 리졸버
 * 신고 뮤테이션에 대한 기능을 제공합니다.
 * 참고: 루트 뮤테이션 진입점은 RootMutationResolver에서 제공하므로 이 클래스에서는 제공하지 않습니다.
 */
@Controller
@Slf4j
public class ReportRootMutationResolver {

    // report() 메서드 제거 - RootMutationResolver에서 이미 제공하고 있음
    
    // 필요한 경우 다른 신고 관련 뮤테이션 메서드를 여기에 추가할 수 있습니다.
    // 예: @SchemaMapping(typeName = "ReportMutation", field = "createReport") 등
}
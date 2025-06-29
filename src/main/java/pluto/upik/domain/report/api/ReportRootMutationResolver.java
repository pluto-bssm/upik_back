package pluto.upik.domain.report.api;

import org.springframework.graphql.data.method.annotation.SchemaMapping;
import pluto.upik.domain.report.data.DTO.ReportMutation;

public class ReportRootMutationResolver {
    @SchemaMapping(typeName = "Mutation", field = "report")
    public ReportMutation report() {
        // Bean이 아닌 객체를 직접 반환하거나 @Component로 ReportMutationResolver를 빈 등록해야 함
        return new ReportMutation(); // 생성자 인자 필요 시 주입
    }
}

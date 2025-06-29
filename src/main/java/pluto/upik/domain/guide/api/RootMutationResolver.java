package pluto.upik.domain.guide.api;

import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.guide.data.DTO.GuideMutation;
import pluto.upik.domain.report.data.DTO.ReportMutation;

@Controller
public class RootMutationResolver {

    @SchemaMapping(typeName = "Mutation", field = "guide")
    public GuideMutation guideMutation() {
        return new GuideMutation(); // 빈 객체로 리턴, 내부 필드에서 처리
    }

    @SchemaMapping(typeName = "Mutation", field = "report")
    public ReportMutation report() {
        return new ReportMutation();  // ReportMutation은 DTO 혹은 빈 POJO
    }
}

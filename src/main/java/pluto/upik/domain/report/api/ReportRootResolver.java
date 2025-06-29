package pluto.upik.domain.report.api;

import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ReportRootResolver {

    @QueryMapping
    public Object report() {
        return new Object(); // 또는 ReportQuery 인스턴스
    }

    @MutationMapping
    public Object reportMutation() {
        return new Object(); // 또는 ReportMutation 인스턴스
    }
}


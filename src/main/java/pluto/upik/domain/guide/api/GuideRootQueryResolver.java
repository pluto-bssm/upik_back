package pluto.upik.domain.guide.api;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GuideRootQueryResolver {

    @QueryMapping
    public Object guide() {
        return new Object(); // 또는 GuideQuery 인스턴스. POJO면 아무거나 가능
    }
}


package pluto.upik.domain.guide.resolver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * GuideRootQueryResolver 클래스에 대한 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class GuideRootQueryResolverTest {

    @InjectMocks
    private GuideRootQueryResolver guideRootQueryResolver;

    @Test
    @DisplayName("guide 쿼리 매핑 테스트")
    void guide_ReturnsObject() {
        // when
        Object result = guideRootQueryResolver.guide();

        // then
        assertNotNull(result, "guide 메서드는 null이 아닌 객체를 반환해야 합니다");
    }
}
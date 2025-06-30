package pluto.upik.domain.guide.resolver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * KeywordGuideRootQueryResolver 클래스에 대한 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class KeywordGuideRootQueryResolverTest {

    @InjectMocks
    private KeywordGuideRootQueryResolver keywordGuideRootQueryResolver;

    @Test
    @DisplayName("keywordGuide 쿼리 매핑 테스트")
    void keywordGuide_ReturnsObject() {
        // when
        Object result = keywordGuideRootQueryResolver.keywordGuide();

        // then
        assertNotNull(result, "keywordGuide 메서드는 null이 아닌 객체를 반환해야 합니다");
    }
}
package pluto.upik.domain.report.resolver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * ReportRootResolver 클래스에 대한 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class ReportRootResolverTest {

    @InjectMocks
    private ReportRootResolver reportRootResolver;

    @Test
    @DisplayName("report 쿼리 매핑 테스트")
    void report_ReturnsObject() {
        // when
        Object result = reportRootResolver.report();

        // then
        assertNotNull(result, "report 메서드는 null이 아닌 객체를 반환해야 합니다");
    }

    @Test
    @DisplayName("reportMutation 뮤테이션 매핑 테스트")
    void reportMutation_ReturnsObject() {
        // when
        Object result = reportRootResolver.reportMutation();

        // then
        assertNotNull(result, "reportMutation 메서드는 null이 아닌 객체를 반환해야 합니다");
    }
}
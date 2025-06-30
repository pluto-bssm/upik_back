package pluto.upik.domain.guide.resolver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import pluto.upik.domain.guide.data.DTO.GuideMutation;
import pluto.upik.domain.report.data.DTO.ReportMutation;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RootMutationResolver 클래스에 대한 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class RootMutationResolverTest {

    @InjectMocks
    private RootMutationResolver rootMutationResolver;

    @Test
    @DisplayName("guideMutation 매핑 테스트")
    void guideMutation_ReturnsGuideMutation() {
        // when
        Object result = rootMutationResolver.guideMutation();

        // then
        assertNotNull(result, "guideMutation 메서드는 null이 아닌 객체를 반환해야 합니다");
        assertTrue(result instanceof GuideMutation, "반환된 객체는 GuideMutation 타입이어야 합니다");
    }

    @Test
    @DisplayName("report 매핑 테스트")
    void report_ReturnsReportMutation() {
        // when
        Object result = rootMutationResolver.report();

        // then
        assertNotNull(result, "report 메서드는 null이 아닌 객체를 반환해야 합니다");
        assertTrue(result instanceof ReportMutation, "반환된 객체는 ReportMutation 타입이어야 합니다");
    }
}
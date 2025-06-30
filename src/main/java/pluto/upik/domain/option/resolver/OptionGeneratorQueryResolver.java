package pluto.upik.domain.option.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.option.data.DTO.GenerateOptionsResponse;
import pluto.upik.domain.option.service.OptionGeneratorServiceInterface;
import pluto.upik.shared.exception.BusinessException;

/**
 * 선택지 생성 GraphQL 쿼리 리졸버
 * 선택지 생성 관련 GraphQL 쿼리를 처리합니다.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class OptionGeneratorQueryResolver {

    private final OptionGeneratorServiceInterface optionGeneratorService;
    
    /**
     * 루트 쿼리에서 선택지 생성 쿼리로 진입하는 메서드
     *
     * @return 선택지 생성 쿼리 객체
     */
    @QueryMapping
    public Object optionGenerator() {
        // 빈 객체 반환 - 실제 처리는 하위 필드에서 수행
        return new Object();
    }
    
    /**
     * 제목과 개수를 받아 선택지를 생성하는 메서드
     *
     * @param parent 부모 객체
     * @param title 제목
     * @param count 생성할 선택지 개수
     * @return 생성된 선택지 응답
     */
    @SchemaMapping(typeName = "OptionGeneratorQuery", field = "generateOptions")
    public GenerateOptionsResponse generateOptions(Object parent, @Argument String title, @Argument int count) {
        log.info("GraphQL 쿼리 - 선택지 생성 요청: 제목={}, 개수={}", title, count);
        
        try {
            // 입력값 검증
            if (title == null || title.trim().isEmpty()) {
                log.warn("GraphQL 쿼리 - 선택지 생성 실패: 제목이 비어있음");
                return GenerateOptionsResponse.builder()
                    .success(false)
                    .message("제목을 입력해주세요.")
                    .build();
            }
            
            if (count < 1 || count > 10) {
                log.warn("GraphQL 쿼리 - 선택지 생성 실패: 유효하지 않은 개수 ({})", count);
                return GenerateOptionsResponse.builder()
                    .success(false)
                    .message("선택지 개수는 1개에서 10개 사이로 입력해주세요.")
                    .build();
            }
            
            // 서비스 호출
            GenerateOptionsResponse response = optionGeneratorService.generateOptions(title, count);
            log.info("GraphQL 쿼리 - 선택지 생성 완료: 제목={}, 개수={}, 성공={}", 
                    title, count, response.isSuccess());
            return response;
        } catch (BusinessException e) {
            log.warn("GraphQL 쿼리 - 선택지 생성 중 비즈니스 오류: 제목={}, 개수={}, 사유={}", 
                    title, count, e.getMessage());
            return GenerateOptionsResponse.builder()
                .success(false)
                .message(e.getMessage())
                .build();
        } catch (Exception e) {
            log.error("GraphQL 쿼리 - 선택지 생성 중 예상치 못한 오류: 제목={}, 개수={}, 오류={}", 
                    title, count, e.getMessage(), e);
            return GenerateOptionsResponse.builder()
                .success(false)
                .message("선택지 생성 중 오류가 발생했습니다.")
                .build();
        }
    }
}
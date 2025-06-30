package pluto.upik.domain.guide.resolver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pluto.upik.domain.guide.data.DTO.KeywordGuideResponse;
import pluto.upik.domain.guide.service.KeywordGuideServiceInterface;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * KeywordGuideResolver 클래스에 대한 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class KeywordGuideResolverTest {

    @Mock
    private KeywordGuideServiceInterface keywordGuideService;

    @InjectMocks
    private KeywordGuideResolver keywordGuideResolver;

    private UUID guideId;
    private String keyword;
    private KeywordGuideResponse keywordGuideResponse;
    private List<KeywordGuideResponse> keywordGuideResponseList;

    @BeforeEach
    void setUp() {
        guideId = UUID.randomUUID();
        keyword = "테스트";
        keywordGuideResponse = KeywordGuideResponse.builder()
                .id(guideId)
                .title("테스트 가이드 제목")
                .keyword(keyword)
                .content("테스트 가이드 내용")
                .createdAt(LocalDate.now())
                .build();
        keywordGuideResponseList = Arrays.asList(keywordGuideResponse);
    }

    @Test
    @DisplayName("키워드로 가이드 검색 성공 테스트")
    void searchByKeyword_Success() {
        // given
        Object parent = new Object();
        when(keywordGuideService.searchGuidesByKeyword(keyword)).thenReturn(keywordGuideResponseList);

        // when
        List<KeywordGuideResponse> result = keywordGuideResolver.searchByKeyword(parent, keyword);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(guideId, result.get(0).getId());
        assertEquals("테스트 가이드 제목", result.get(0).getTitle());
        assertEquals(keyword, result.get(0).getKeyword());
        assertEquals("테스트 가이드 내용", result.get(0).getContent());
        verify(keywordGuideService).searchGuidesByKeyword(keyword);
    }
    
    @Test
    @DisplayName("키워드로 가이드 검색 - 리소스 없음 예외 테스트")
    void searchByKeyword_ResourceNotFound() {
        // given
        Object parent = new Object();
        when(keywordGuideService.searchGuidesByKeyword(keyword))
            .thenThrow(new ResourceNotFoundException("해당 키워드로 검색된 가이드가 없습니다: " + keyword));

        // when & then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> keywordGuideResolver.searchByKeyword(parent, keyword));
            
        assertEquals("해당 키워드로 검색된 가이드가 없습니다: " + keyword, exception.getMessage());
        verify(keywordGuideService).searchGuidesByKeyword(keyword);
    }
    
    @Test
    @DisplayName("키워드로 가이드 검색 - 비즈니스 예외 테스트")
    void searchByKeyword_BusinessException() {
        // given
        Object parent = new Object();
        when(keywordGuideService.searchGuidesByKeyword(keyword))
            .thenThrow(new BusinessException("가이드 키워드 검색 중 오류가 발생했습니다."));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> keywordGuideResolver.searchByKeyword(parent, keyword));
            
        assertEquals("가이드 키워드 검색 중 오류가 발생했습니다.", exception.getMessage());
        verify(keywordGuideService).searchGuidesByKeyword(keyword);
    }
}
package pluto.upik.domain.guide.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pluto.upik.domain.guide.data.DTO.KeywordGuideResponse;
import pluto.upik.domain.guide.data.model.Guide;
import pluto.upik.domain.guide.repository.GuideRepository;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * KeywordGuideService 클래스에 대한 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class KeywordGuideServiceTest {

    @Mock
    private GuideRepository guideRepository;

    @InjectMocks
    private KeywordGuideService keywordGuideService;

    private UUID guideId;
    private Guide testGuide;
    private String keyword;

    @BeforeEach
    void setUp() {
        guideId = UUID.randomUUID();
        keyword = "테스트";
        testGuide = new Guide();
        testGuide.setId(guideId);
        testGuide.setTitle("테스트 가이드 제목");
        testGuide.setContent("테스트 가이드 내용");
        testGuide.setCreatedAt(LocalDate.now());
        testGuide.setCategory("테스트 카테고리");
    }

    @Test
    @DisplayName("키워드로 가이드 검색 성공 테스트")
    void searchGuidesByKeyword_Success() {
        // given
        List<Guide> guideList = Arrays.asList(testGuide);
        when(guideRepository.findGuidesByTitleContaining(keyword)).thenReturn(guideList);

        // when
        List<KeywordGuideResponse> result = keywordGuideService.searchGuidesByKeyword(keyword);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(guideId, result.get(0).getId());
        assertEquals("테스트 가이드 제목", result.get(0).getTitle());
        assertEquals(keyword, result.get(0).getKeyword());
        assertEquals("테스트 가이드 내용", result.get(0).getContent());
        verify(guideRepository).findGuidesByTitleContaining(keyword);
    }

    @Test
    @DisplayName("키워드로 가이드 검색 - 결과 없음 테스트")
    void searchGuidesByKeyword_NoResults() {
        // given
        when(guideRepository.findGuidesByTitleContaining(keyword)).thenReturn(Collections.emptyList());

        // when & then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> keywordGuideService.searchGuidesByKeyword(keyword));
            
        assertEquals("해당 키워드로 검색된 가이드가 없습니다: " + keyword, exception.getMessage());
        verify(guideRepository).findGuidesByTitleContaining(keyword);
    }

    @Test
    @DisplayName("키워드로 가이드 검색 - 예외 발생 테스트")
    void searchGuidesByKeyword_Exception() {
        // given
        when(guideRepository.findGuidesByTitleContaining(keyword)).thenThrow(new RuntimeException("DB 오류"));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> keywordGuideService.searchGuidesByKeyword(keyword));
            
        assertEquals("가이드 키워드 검색 중 오류가 발생했습니다.", exception.getMessage());
        verify(guideRepository).findGuidesByTitleContaining(keyword);
    }
    
    @Test
    @DisplayName("KeywordGuideResponse의 추가 기능 테스트")
    void keywordGuideResponseFunctions_Test() {
        // given
        List<Guide> guideList = Arrays.asList(testGuide);
        when(guideRepository.findGuidesByTitleContaining(keyword)).thenReturn(guideList);
        
        // when
        List<KeywordGuideResponse> result = keywordGuideService.searchGuidesByKeyword(keyword);
        KeywordGuideResponse response = result.get(0);
        
        // then
        assertEquals("테스트 가이드 내용", response.getContentSummary(100));
        assertEquals("테스트 가...", response.getContentSummary(5));
        assertEquals("테스트 가이드 제목", response.getTitle());
        assertEquals("<strong>테스트</strong> 가이드 제목", response.getHighlightedTitle());
    }
}
package pluto.upik.domain.guide.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pluto.upik.domain.guide.data.DTO.GuideDetailResponse;
import pluto.upik.domain.guide.data.DTO.GuideResponse;
import pluto.upik.domain.guide.data.model.Guide;
import pluto.upik.domain.guide.repository.GuideRepository;
import pluto.upik.domain.vote.data.model.Vote;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * GuideQueryService 클래스에 대한 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class GuideQueryServiceTest {

    @Mock
    private GuideRepository guideRepository;

    @InjectMocks
    private GuideQueryService guideQueryService;

    private UUID guideId;
    private UUID voteId;
    private Guide testGuide;
    private String category;

    @BeforeEach
    void setUp() {
        guideId = UUID.randomUUID();
        voteId = UUID.randomUUID();
        category = "테스트 카테고리";
        
        Vote vote = new Vote();
        vote.setId(voteId);
        
        testGuide = new Guide();
        testGuide.setId(guideId);
        testGuide.setTitle("테스트 가이드 제목");
        testGuide.setContent("테스트 가이드 내용");
        testGuide.setCreatedAt(LocalDate.now());
        testGuide.setCategory(category);
        testGuide.setGuideType("일반");
        testGuide.setLike(10L);
        testGuide.setRevoteCount(5L);
        testGuide.setVote(vote);
    }

    @Test
    @DisplayName("카테고리별 가이드 조회 성공 테스트")
    void findByCategory_Success() {
        // given
        List<Guide> guideList = Arrays.asList(testGuide);
        when(guideRepository.findAllByCategory(category)).thenReturn(guideList);

        // when
        List<GuideResponse> result = guideQueryService.findByCategory(category);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(guideId, result.get(0).getId());
        assertEquals("테스트 가이드 제목", result.get(0).getTitle());
        assertEquals("테스트 가이드 내용", result.get(0).getContent());
        assertEquals(10L, result.get(0).getLikeCount());
        verify(guideRepository).findAllByCategory(category);
    }

    @Test
    @DisplayName("카테고리별 가이드 조회 - 결과 없음 테스트")
    void findByCategory_NoResults() {
        // given
        when(guideRepository.findAllByCategory(category)).thenReturn(Collections.emptyList());

        // when & then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> guideQueryService.findByCategory(category));
            
        assertEquals("카테고리에 해당하는 가이드가 없습니다: " + category, exception.getMessage());
        verify(guideRepository).findAllByCategory(category);
    }

    @Test
    @DisplayName("카테고리별 가이드 조회 - 예외 발생 테스트")
    void findByCategory_Exception() {
        // given
        when(guideRepository.findAllByCategory(category)).thenThrow(new RuntimeException("DB 오류"));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> guideQueryService.findByCategory(category));
            
        assertEquals("가이드 조회 중 오류가 발생했습니다.", exception.getMessage());
        verify(guideRepository).findAllByCategory(category);
    }
    
    @Test
    @DisplayName("가이드 ID로 상세 조회 성공 테스트")
    void findGuideById_Success() {
        // given
        when(guideRepository.findById(guideId)).thenReturn(Optional.of(testGuide));
        
        // when
        GuideDetailResponse result = guideQueryService.findGuideById(guideId);
        
        // then
        assertNotNull(result);
        assertEquals(guideId, result.getId());
        assertEquals("테스트 가이드 제목", result.getTitle());
        assertEquals("테스트 가이드 내용", result.getContent());
        assertEquals(category, result.getCategory());
        assertEquals("일반", result.getGuideType());
        assertEquals(10L, result.getLikeCount());
        assertEquals(5L, result.getRevoteCount());
        assertEquals(voteId, result.getVoteId());
        verify(guideRepository).findById(guideId);
    }
    
    @Test
    @DisplayName("가이드 ID로 상세 조회 - 결과 없음 테스트")
    void findGuideById_NotFound() {
        // given
        when(guideRepository.findById(guideId)).thenReturn(Optional.empty());
        
        // when & then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> guideQueryService.findGuideById(guideId));
            
        assertEquals("해당 ID의 가이드를 찾을 수 없습니다: " + guideId, exception.getMessage());
        verify(guideRepository).findById(guideId);
    }
    
    @Test
    @DisplayName("가이드 ID로 상세 조회 - 예외 발생 테스트")
    void findGuideById_Exception() {
        // given
        when(guideRepository.findById(guideId)).thenThrow(new RuntimeException("DB 오류"));
        
        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
            () -> guideQueryService.findGuideById(guideId));
            
        assertEquals("가이드 상세 조회 중 오류가 발생했습니다.", exception.getMessage());
        verify(guideRepository).findById(guideId);
    }
}
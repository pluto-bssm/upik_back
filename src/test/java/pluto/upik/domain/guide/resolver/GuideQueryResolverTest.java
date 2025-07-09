package pluto.upik.domain.guide.resolver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pluto.upik.domain.guide.data.DTO.GuideDetailResponse;
import pluto.upik.domain.guide.data.DTO.GuideResponse;
import pluto.upik.domain.guide.service.GuideQueryServiceInterface;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * GuideQueryResolver 클래스에 대한 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class GuideQueryResolverTest {

    @Mock
    private GuideQueryServiceInterface guideQueryService;

    @InjectMocks
    private GuideQueryResolver guideQueryResolver;

    private UUID guideId;
    private UUID voteId;
    private String category;
    private GuideResponse guideResponse;
    private List<GuideResponse> guideResponseList;
    private GuideDetailResponse guideDetailResponse;
    private Object parent;

    @BeforeEach
    void setUp() {
        guideId = UUID.randomUUID();
        voteId = UUID.randomUUID();
        category = "테스트 카테고리";
        
        guideResponse = GuideResponse.builder()
                .id(guideId)
                .title("테스트 가이드 제목")
                .content("테스트 가이드 내용")
                .createdAt(LocalDate.now())
                .likeCount(10L)
                .build();
                
        guideResponseList = Arrays.asList(guideResponse);
        
        guideDetailResponse = GuideDetailResponse.builder()
                .id(guideId)
                .title("테스트 가이드 제목")
                .content("테스트 가이드 내용")
                .createdAt(LocalDate.now())
                .category(category)
                .guideType("일반")
                .likeCount(10L)
                .revoteCount(5L)
                .voteId(voteId)
                .build();
                
        parent = new Object();
    }

    @Test
    @DisplayName("카테고리별 가이드 조회 테스트")
    void guidesByCategory_Success() {
        // given
        when(guideQueryService.findByCategory(category)).thenReturn(guideResponseList);

        // when
        List<GuideResponse> result = guideQueryResolver.guidesByCategory(parent, category);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(guideId, result.get(0).getId());
        assertEquals("테스트 가이드 제목", result.get(0).getTitle());
        assertEquals("테스트 가이드 내용", result.get(0).getContent());
        assertEquals(10L, result.get(0).getLikeCount());
        verify(guideQueryService).findByCategory(category);
    }
    
    @Test
    @DisplayName("가이드 ID로 상세 조회 성공 테스트")
    void guideById_Success() {
        // given
        String guideIdString = guideId.toString();
        when(guideQueryService.findGuideById(guideId)).thenReturn(guideDetailResponse);
        
        // when
        GuideDetailResponse result = guideQueryResolver.guideById(parent, guideIdString);
        
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
        verify(guideQueryService).findGuideById(guideId);
    }
    
    @Test
    @DisplayName("가이드 ID로 상세 조회 - 잘못된 UUID 형식 테스트")
    void guideById_InvalidUUID() {
        // given
        String invalidId = "invalid-uuid";
        
        // when & then
        assertThrows(IllegalArgumentException.class, () -> 
            guideQueryResolver.guideById(parent, invalidId));
    }
    
    @Test
    @DisplayName("가이드 ID로 상세 조회 - 리소스 없음 예외 테스트")
    void guideById_ResourceNotFound() {
        // given
        String guideIdString = guideId.toString();
        when(guideQueryService.findGuideById(guideId))
            .thenThrow(new ResourceNotFoundException("해당 ID의 가이드를 찾을 수 없습니다: " + guideId));
        
        // when & then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> 
            guideQueryResolver.guideById(parent, guideIdString));
            
        assertEquals("해당 ID의 가이드를 찾을 수 없습니다: " + guideId, exception.getMessage());
        verify(guideQueryService).findGuideById(guideId);
    }
    
    @Test
    @DisplayName("가이드 ID로 상세 조회 - 비즈니스 예외 테스트")
    void guideById_BusinessException() {
        // given
        String guideIdString = guideId.toString();
        when(guideQueryService.findGuideById(guideId))
            .thenThrow(new BusinessException("가이드 상세 조회 중 오류가 발생했습니다."));
        
        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> 
            guideQueryResolver.guideById(parent, guideIdString));
            
        assertEquals("가이드 상세 조회 중 오류가 발생했습니다.", exception.getMessage());
        verify(guideQueryService).findGuideById(guideId);
    }
}
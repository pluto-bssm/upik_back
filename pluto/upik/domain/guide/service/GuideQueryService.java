package pluto.upik.domain.guide.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pluto.upik.domain.guide.data.DTO.GuideDetailResponse;
import pluto.upik.domain.guide.data.DTO.GuideResponse;
import pluto.upik.domain.guide.data.model.Guide;
import pluto.upik.domain.guide.repository.GuideRepository;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.exception.ResourceNotFoundException;
import pluto.upik.shared.util.ValidationUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 가이드 조회 관련 비즈니스 로직을 처리하는 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GuideQueryService implements GuideQueryServiceInterface {

    private final GuideRepository guideRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<GuideResponse> findByCategory(String category) {
        log.debug("카테고리별 가이드 조회 시작 - category: {}", category);

        try {
            List<Guide> guides = guideRepository.findAllByCategory(category);
            
            if (guides.isEmpty()) {
                log.info("카테고리별 가이드 없음 - category: {}", category);
                throw new ResourceNotFoundException("카테고리에 해당하는 가이드가 없습니다: " + category);
            }

            List<GuideResponse> responses = guides.stream()
                    .map(this::mapToGuideResponse)
                    .collect(Collectors.toList());

            log.debug("카테고리별 가이드 조회 완료 - category: {}, 결과 개수: {}", category, responses.size());
            return responses;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("카테고리별 가이드 조회 중 오류 - category: {}, error: {}", category, e.getMessage(), e);
            throw new BusinessException("가이드 조회 중 오류가 발생했습니다.");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GuideDetailResponse findGuideById(UUID guideId) {
        log.debug("가이드 상세 조회 시작 - guideId: {}", guideId);
        
        try {
            Optional<Guide> guideOptional = guideRepository.findById(guideId);
            
            ValidationUtils.validateCondition(guideOptional.isPresent(), 
                    () -> new ResourceNotFoundException("해당 ID의 가이드를 찾을 수 없습니다: " + guideId));
            
            Guide guide = guideOptional.get();
            GuideDetailResponse response = mapToGuideDetailResponse(guide);
            
            log.debug("가이드 상세 조회 완료 - guideId: {}", guideId);
            return response;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("가이드 상세 조회 중 오류 - guideId: {}, error: {}", guideId, e.getMessage(), e);
            throw new BusinessException("가이드 상세 조회 중 오류가 발생했습니다.");
        }
    }
    
    /**
     * Guide 엔티티를 GuideResponse DTO로 변환합니다.
     * 
     * @param guide 변환할 Guide 엔티티
     * @return 변환된 GuideResponse 객체
     */
    private GuideResponse mapToGuideResponse(Guide guide) {
        return GuideResponse.builder()
                .id(guide.getId())
                .title(guide.getTitle())
                .content(guide.getContent())
                .createdAt(guide.getCreatedAt())
                .like(guide.getLike() != null ? guide.getLike().intValue() : 0)
                .build();
    }
    
    /**
     * Guide 엔티티를 GuideDetailResponse DTO로 변환합니다.
     * 
     * @param guide 변환할 Guide 엔티티
     * @return 변환된 GuideDetailResponse 객체
     */
    private GuideDetailResponse mapToGuideDetailResponse(Guide guide) {
        return GuideDetailResponse.builder()
                .id(guide.getId())
                .title(guide.getTitle())
                .content(guide.getContent())
                .createdAt(guide.getCreatedAt())
                .category(guide.getCategory())
                .guideType(guide.getGuideType())
                .likeCount(guide.getLike() != null ? guide.getLike().intValue() : 0)
                .revoteCount(guide.getRevoteCount() != null ? guide.getRevoteCount().intValue() : 0)
                .voteId(guide.getVote() != null ? guide.getVote().getId() : null)
                .build();
    }
}
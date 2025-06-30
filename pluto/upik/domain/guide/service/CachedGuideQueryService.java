package pluto.upik.domain.guide.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import pluto.upik.domain.guide.data.DTO.GuideDetailResponse;
import pluto.upik.domain.guide.data.DTO.GuideResponse;

import java.util.List;
import java.util.UUID;

/**
 * 캐싱을 적용한 가이드 조회 서비스
 * 기존 GuideQueryService를 래핑하고 캐싱 기능을 추가합니다.
 */
@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class CachedGuideQueryService implements GuideQueryServiceInterface {

    private final GuideQueryService guideQueryService;

    /**
     * {@inheritDoc}
     * 카테고리별 가이드 조회 결과를 캐싱합니다.
     */
    @Override
    @Cacheable(value = "guides", key = "#category")
    public List<GuideResponse> findByCategory(String category) {
        log.debug("카테고리별 가이드 캐시 조회 시도 - category: {}", category);
        return guideQueryService.findByCategory(category);
    }

    /**
     * {@inheritDoc}
     * 가이드 상세 조회 결과를 캐싱합니다.
     */
    @Override
    @Cacheable(value = "guideDetails", key = "#guideId")
    public GuideDetailResponse findGuideById(UUID guideId) {
        log.debug("가이드 상세 캐시 조회 시도 - guideId: {}", guideId);
        return guideQueryService.findGuideById(guideId);
    }
}
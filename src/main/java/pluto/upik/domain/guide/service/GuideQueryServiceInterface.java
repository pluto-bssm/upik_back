package pluto.upik.domain.guide.service;

import pluto.upik.domain.guide.data.DTO.GuideDetailResponse;
import pluto.upik.domain.guide.data.DTO.GuideResponse;

import java.util.List;
import java.util.UUID;

/**
 * 가이드 조회 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface GuideQueryServiceInterface {

    /**
     * 특정 카테고리에 속한 가이드 목록을 조회합니다.
     *
     * @param category 조회할 카테고리
     * @return 가이드 응답 목록
     * @throws pluto.upik.shared.exception.ResourceNotFoundException 카테고리에 해당하는 가이드가 없을 경우 발생
     * @throws pluto.upik.shared.exception.BusinessException 조회 중 오류 발생 시
     */
    List<GuideResponse> findByCategory(String category);
    
    /**
     * 특정 ID의 가이드를 상세 조회합니다.
     *
     * @param guideId 조회할 가이드 ID
     * @return 가이드 상세 응답
     * @throws pluto.upik.shared.exception.ResourceNotFoundException 가이드가 존재하지 않을 경우 발생
     * @throws pluto.upik.shared.exception.BusinessException 조회 중 오류 발생 시
     */
    GuideDetailResponse findGuideById(UUID guideId);
}
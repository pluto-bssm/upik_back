package pluto.upik.domain.guide.service;

import pluto.upik.domain.guide.data.DTO.KeywordGuideResponse;

import java.util.List;

/**
 * 키워드 기반 가이드 검색 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface KeywordGuideServiceInterface {

    /**
     * 특정 키워드가 포함된 가이드 목록을 검색합니다.
     *
     * @param keyword 검색할 키워드
     * @return 키워드 가이드 응답 목록
     * @throws pluto.upik.shared.exception.ResourceNotFoundException 키워드에 해당하는 가이드가 없을 경우 발생
     * @throws pluto.upik.shared.exception.BusinessException 검색 중 오류 발생 시
     */
    List<KeywordGuideResponse> searchGuidesByKeyword(String keyword);
}
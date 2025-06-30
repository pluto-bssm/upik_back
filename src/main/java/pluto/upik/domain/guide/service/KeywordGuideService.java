package pluto.upik.domain.guide.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pluto.upik.domain.guide.data.DTO.KeywordGuideResponse;
import pluto.upik.domain.guide.data.model.Guide;
import pluto.upik.domain.guide.repository.GuideRepository;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.exception.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 키워드 기반 가이드 검색 관련 비즈니스 로직을 처리하는 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KeywordGuideService implements KeywordGuideServiceInterface {

    private final GuideRepository guideRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<KeywordGuideResponse> searchGuidesByKeyword(String keyword) {
        log.info("키워드 기반 가이드 검색 요청 시작 - keyword: {}", keyword);

        try {
            List<Guide> guides = guideRepository.findGuidesByTitleContaining(keyword);
            log.info("키워드 기반 가이드 검색 결과 - keyword: {}, 검색된 가이드 수: {}", keyword, guides.size());

            if (guides == null || guides.isEmpty()) {
                log.warn("키워드 기반 가이드 검색 실패 - 가이드 없음 (keyword: {})", keyword);
                throw new ResourceNotFoundException("해당 키워드로 검색된 가이드가 없습니다: " + keyword);
            }

            List<KeywordGuideResponse> responses = guides.stream()
                    .map(g -> KeywordGuideResponse.builder()
                            .id(g.getId())
                            .title(g.getTitle())
                            .keyword(keyword)
                            .content(g.getContent())
                            .createdAt(g.getCreatedAt())
                            .build())
                    .collect(Collectors.toList());

            log.info("키워드 기반 가이드 검색 완료 - keyword: {}, 결과 개수: {}", keyword, responses.size());
            return responses;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("가이드 키워드 검색 중 예외 발생 - keyword: {}, error: {}", keyword, e.getMessage(), e);
            throw new BusinessException("가이드 키워드 검색 중 오류가 발생했습니다.");
        }
    }
}
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

@Slf4j
@Service
@RequiredArgsConstructor
public class KeywordGuideService {

    private final GuideRepository guideRepository;

    public List<KeywordGuideResponse> searchGuidesByKeyword(String keyword) {
        log.info("searchGuidesByKeyword called with keyword: {}", keyword);

        try {
            List<Guide> guides = guideRepository.findGuidesByTitleContaining(keyword);
            log.info("Number of guides found: {}", guides.size());

            if (guides == null || guides.isEmpty()) {
                throw new ResourceNotFoundException("해당 키워드로 검색된 가이드가 없습니다: " + keyword);
            }

            return guides.stream()
                    .map(g -> new KeywordGuideResponse(
                            g.getId(),
                            g.getTitle(),
                            keyword,
                            g.getContent(),
                            g.getCreatedAt()
                    ))
                    .collect(Collectors.toList());
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("가이드 키워드 검색 중 예외 발생: {}", e.getMessage(), e);
            throw new BusinessException("가이드 키워드 검색 중 오류가 발생했습니다.");
        }
    }
}

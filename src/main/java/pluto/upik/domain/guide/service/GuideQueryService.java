package pluto.upik.domain.guide.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pluto.upik.domain.guide.data.DTO.GuideResponse;
import pluto.upik.domain.guide.data.model.Guide;
import pluto.upik.domain.guide.repository.GuideRepository;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.exception.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuideQueryService {

    private final GuideRepository guideRepository;

    public List<GuideResponse> findByCategory(String category) {
        log.info("findByCategory called with category: {}", category);

        try {
            List<Guide> guides = guideRepository.findAllByCategory(category);
            log.info("Number of guides found: {}", guides.size());

            if (guides == null || guides.isEmpty()) {
                throw new ResourceNotFoundException("카테고리에 해당하는 가이드가 없습니다: " + category);
            }

            return guides.stream()
                    .map(guide -> new GuideResponse(
                            guide.getId(),
                            guide.getTitle(),
                            guide.getContent(),
                            guide.getCreatedAt(),
                            guide.getLike()
                    ))
                    .collect(Collectors.toList());
        } catch (ResourceNotFoundException e) {
            throw e; // 위에서 던진 거 그대로 전달
        } catch (Exception e) {
            log.error("Guide 조회 중 예외 발생: {}", e.getMessage(), e);
            throw new BusinessException("가이드 조회 중 오류가 발생했습니다.");
        }
    }
}

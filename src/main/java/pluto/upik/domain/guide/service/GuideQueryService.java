package pluto.upik.domain.guide.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pluto.upik.domain.guide.data.DTO.GuideResponse;
import pluto.upik.domain.guide.data.model.Guide;
import pluto.upik.domain.guide.repository.GuideRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuideQueryService {

    private final GuideRepository guideRepository;

    public List<GuideResponse> findByCategory(String category) {
        log.info("findByCategory called with category: {}", category);  // 카테고리 값 로그 찍기
        List<Guide> guides = guideRepository.findAllByCategory(category);
        log.info("Number of guides found: {}", guides.size());  // 결과 개수 로그 찍기

        return guides.stream()
                .map(guide -> new GuideResponse(
                        guide.getId(),
                        guide.getTitle(),
                        guide.getContent(),
                        guide.getCreatedAt(),
                        guide.getLike()
                ))
                .collect(Collectors.toList());
    }
}

package pluto.upik.domain.guide.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pluto.upik.domain.guide.data.DTO.KeywordGuideResponse;
import pluto.upik.domain.guide.data.model.Guide;
import pluto.upik.domain.guide.repository.GuideRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeywordGuideService {

    private final GuideRepository guideRepository;

    public List<KeywordGuideResponse> searchGuidesByKeyword(String keyword) {
        log.info("searchGuidesByKeyword called with keyword: {}", keyword);
        List<Guide> guides = guideRepository.findGuidesByTitleContaining(keyword);
        log.info("Number of guides found: {}", guides.size());

        return guides.stream()
                .map(g -> new KeywordGuideResponse(
                        g.getId(),
                        g.getTitle(),
                        keyword,
                        g.getContent(),
                        g.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}

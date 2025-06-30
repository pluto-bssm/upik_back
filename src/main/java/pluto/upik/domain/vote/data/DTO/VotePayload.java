package pluto.upik.domain.vote.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pluto.upik.domain.option.data.model.Option;
import pluto.upik.domain.vote.data.model.Vote;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotePayload {
    private UUID id;
    private String title;  // 스키마와 일치하도록 question -> title로 변경
    private String category;
    private String status;
    private List<OptionPayload> options;

    // 정적 팩토리 메서드
    public static VotePayload fromEntity(Vote vote, List<Option> options) {
        return VotePayload.builder()
                .id(vote.getId())
                .title(vote.getQuestion())  // question 필드를 title로 매핑
                .category(vote.getCategory())
                .status(vote.getStatus().name())
                .options(options.stream()
                        .map(option -> new OptionPayload(option.getId(), option.getContent()))
                        .collect(Collectors.toList()))
                .build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionPayload {
        private UUID id;
        private String content;
    }
}
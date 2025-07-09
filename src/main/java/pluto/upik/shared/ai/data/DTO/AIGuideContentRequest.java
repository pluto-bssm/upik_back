package pluto.upik.shared.ai.data.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AI 가이드 내용 생성 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIGuideContentRequest {
    @NotBlank(message = "투표 제목은 필수입니다")
    private String voteTitle;

    @NotBlank(message = "선택지 내용은 필수입니다")
    private String optionContent;

    private String category;
}

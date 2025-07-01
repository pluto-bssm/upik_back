package pluto.upik.shared.ai.data.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AI 질문 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIQuestionRequest {
    @NotBlank(message = "질문 내용은 필수입니다")
    private String question;
}

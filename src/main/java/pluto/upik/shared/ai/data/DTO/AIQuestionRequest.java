package pluto.upik.shared.ai.data.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AIQuestionRequest {
    @NotBlank(message = "질문 내용은 필수입니다")
    private String question;
}


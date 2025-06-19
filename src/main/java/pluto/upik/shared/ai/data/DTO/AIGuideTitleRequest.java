package pluto.upik.shared.ai.data.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AIGuideTitleRequest {
    @NotBlank(message = "투표 제목은 필수입니다")
    private String voteTitle;
    private String voteDescription;
}


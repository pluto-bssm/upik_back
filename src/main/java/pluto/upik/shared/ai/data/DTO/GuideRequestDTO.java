package pluto.upik.shared.ai.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuideRequestDTO {
    private UUID voteId;
    private String voteTitle;
    private String voteDescription;
    private String optionsWithPercents;
    private String type;
    private String tailQuestion;
    private String tailResponses;
}

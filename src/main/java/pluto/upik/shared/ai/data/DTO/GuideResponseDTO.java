package pluto.upik.shared.ai.data.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuideResponseDTO {
    private UUID id;
    private UUID voteId;
    private String title;
    private String content;
    private LocalDate createdAt;
    private String category;
    private String guideType;
    private Long revoteCount;
    private Long like;
}

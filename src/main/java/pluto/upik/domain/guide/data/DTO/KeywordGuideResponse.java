package pluto.upik.domain.guide.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KeywordGuideResponse {
    private UUID id;
    private String title;
    private String keyword;
    private String content;
    private LocalDate createdAt;
}

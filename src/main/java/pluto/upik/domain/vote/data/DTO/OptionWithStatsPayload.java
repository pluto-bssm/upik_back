package pluto.upik.domain.vote.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionWithStatsPayload {
    private UUID id;
    private String content;
    private int responseCount;
    private float percentage;
}
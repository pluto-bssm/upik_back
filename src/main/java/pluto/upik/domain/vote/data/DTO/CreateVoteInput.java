package pluto.upik.domain.vote.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class CreateVoteInput {
    private String title;
    private String category;
    private List<String> options;
}
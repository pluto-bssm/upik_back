package pluto.upik.domain.report.data.DTO;

import lombok.Data;

@Data
public class SubmitReportInput {
    private String userId;
    private String targetId;
    private String reason;
}


package pluto.upik.domain.report.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ReportResponse {
    private UUID userId; // 신고자 ID
    private UUID targetId; // 신고 대상 ID
    private String reason; // 신고 이유
    private LocalDate createdAt; // 신고 생성 날짜
}

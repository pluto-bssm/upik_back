package pluto.upik.domain.report.data.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 신고 관련 GraphQL 뮤테이션 루트 객체
 * 스키마의 ReportMutation 타입에 매핑됩니다.
 */
@Data
@NoArgsConstructor
public class ReportMutation {
    // 이 클래스는 GraphQL 스키마의 ReportMutation 타입에 매핑되는 빈 컨테이너 역할을 합니다.
    // 실제 뮤테이션 처리는 ReportMutationResolver에서 이루어집니다.
}
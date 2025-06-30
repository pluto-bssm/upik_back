package pluto.upik.domain.guide.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 가이드 상세 정보를 클라이언트에 전달하기 위한 DTO 클래스
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class GuideDetailResponse {
    /**
     * 가이드 ID
     */
    private UUID id;
    
    /**
     * 가이드 제목
     */
    private String title;
    
    /**
     * 가이드 내용
     */
    private String content;
    
    /**
     * 가이드 생성 일자
     */
    private LocalDate createdAt;
    
    /**
     * 가이드 카테고리
     */
    private String category;
    
    /**
     * 가이드 타입
     */
    private String guideType;
    
    /**
     * 좋아요 수
     */
    private int likeCount;

    /**
     * 재투표 수
     */
    private int revoteCount;

    /**
     * 관련 투표 ID
     */
    private UUID voteId;

    // Builder 패턴에서 기본값 설정
    public static class GuideDetailResponseBuilder {
        private int likeCount = 0;
        private int revoteCount = 0;
}
}

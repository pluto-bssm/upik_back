package pluto.upik.domain.vote.resolver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pluto.upik.domain.vote.data.model.Vote;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 투표 응답 DTO
 * 투표 정보를 클라이언트에 반환하기 위한 데이터 전송 객체입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteResponse {

    /**
     * 투표 ID
     */
    @Schema(description = "투표 ID")
    private UUID id;

    /**
     * 투표 질문
     */
    @Schema(description = "투표 질문")
    private String question;

    /**
     * 투표 카테고리
     */
    @Schema(description = "투표 카테고리")
    private String category;

    /**
     * 투표 상태
     */
    @Schema(description = "투표 상태")
    private Vote.Status status;

    /**
     * 투표 종료 일자
     */
    @Schema(description = "투표 종료 일자")
    private LocalDate finishedAt;

    /**
     * 투표 생성자 ID
     */
    @Schema(description = "투표 생성자 ID")
    private UUID creatorId;


    /**
     * 투표 엔티티를 DTO로 변환합니다.
     *
     * @param vote 투표 엔티티
     * @return 투표 응답 DTO
     */
    public static VoteResponse fromEntity(Vote vote) {
        return VoteResponse.builder()
                .id(vote.getId())
                .question(vote.getQuestion())
                .category(vote.getCategory())
                .status(vote.getStatus())
                .finishedAt(vote.getFinishedAt())
                .creatorId(vote.getUser().getId())
                .build();
    }
}

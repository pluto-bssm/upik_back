package pluto.upik.domain.vote.resolver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 투표 생성 요청 DTO
 * 투표 생성 API 요청에 사용되는 데이터 전송 객체입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateVoteRequest {

    /**
     * 투표 질문
     */
    @NotBlank(message = "질문은 필수 입력값입니다")
    @Schema(description = "투표 질문", example = "가장 좋아하는 프로그래밍 언어는?")
    private String question;

    /**
     * 투표 카테고리
     */
    @NotNull(message = "카테고리는 필수 입력값입니다")
    @Schema(description = "투표 카테고리", example = "A")
    private String category;

    /**
     * 투표 종료 일자
     */
    @NotNull(message = "종료 일자는 필수 입력값입니다")
    @Future(message = "종료 일자는 미래 날짜여야 합니다")
    @Schema(description = "투표 종료 일자", example = "2023-12-31")
    private LocalDate finishedAt;
}

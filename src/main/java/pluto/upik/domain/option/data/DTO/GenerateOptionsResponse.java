package pluto.upik.domain.option.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 선택지 생성 응답 DTO
 * AI가 생성한 선택지 목록을 담는 응답 객체입니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateOptionsResponse {
    /**
     * 성공 여부
     */
    private boolean success;
    
    /**
     * 응답 메시지
     */
    private String message;
    
    /**
     * 생성된 선택지 목록
     */
    private List<String> options;
}
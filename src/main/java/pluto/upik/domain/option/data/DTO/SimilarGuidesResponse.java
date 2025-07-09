package pluto.upik.domain.option.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 유사 가이드 검색 응답 DTO
 * 제목과 유사한 가이드 검색 결과를 담는 응답 객체입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimilarGuidesResponse {
    
    /**
     * 성공 여부
     */
    private boolean success;
    
    /**
     * 응답 메시지
     */
    private String message;
    
    /**
     * 찾은 가이드 목록
     */
    private List<GuideSimpleInfo> guides;
    
    /**
     * 검색된 가이드 수
     */
    private int count;
}
package pluto.upik.domain.option.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 간단한 가이드 정보 DTO
 * 가이드의 기본 정보만 포함하는 간략화된 DTO입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuideSimpleInfo {
    
    /**
     * 가이드 ID
     */
    private UUID id;
    
    /**
     * 가이드 제목
     */
    private String title;
    
    /**
     * 가이드 요약 내용
     */
    private String summary;
}
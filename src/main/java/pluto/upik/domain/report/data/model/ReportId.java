package pluto.upik.domain.report.data.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Report 엔티티의 복합 기본키를 정의하는 클래스
 * userId와 targetId로 구성된 복합키를 관리합니다.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReportId implements Serializable {
    
    /**
     * 신고한 사용자의 ID
     */
    private UUID userId;
    
    /**
     * 신고 대상의 ID
     */
    private UUID targetId;

    /**
     * 복합키 비교를 위한 equals 메서드
     * 
     * @param o 비교할 객체
     * @return 동일한 복합키인 경우 true, 그렇지 않으면 false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReportId)) return false;
        ReportId that = (ReportId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(targetId, that.targetId);
    }

    /**
     * 복합키의 해시코드를 생성하는 메서드
     * 
     * @return 복합키의 해시코드
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId, targetId);
    }
    
    /**
     * 복합키의 문자열 표현을 반환하는 메서드
     * 
     * @return 복합키의 문자열 표현
     */
    @Override
    public String toString() {
        return "ReportId{" +
                "userId=" + userId +
                ", targetId=" + targetId +
                '}';
    }
}
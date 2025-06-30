package pluto.upik.domain.user.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 사용자 엔티티
 * 사용자 정보를 저장하는 엔티티입니다.
 */
@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * 사용자 ID (기본 키)
     */
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    /**
     * 사용자 역할
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "enum ('ADMIN', 'USER')")
    private Role role;

    /**
     * 사용자명
     */
    @Column(nullable = false, length = 50)
    private String username;

    /**
     * 이름
     */
    @Column(length = 30)
    private String name;

    /**
     * 이메일
     */
    @Column(length = 100)
    private String email;

    /**
     * 생성 일시
     */
    @Column
    private LocalDate createdAt;

    /**
     * 달러 보유량
     */
    @Column
    private Double dollar;

    /**
     * 원화 보유량
     */
    @Column
    private Double won;

    /**
     * 연속 접속 일수
     */
    @Column
    private Long streakCount;

    /**
     * 최근 접속 일자
     */
    @Column
    private LocalDate recentDate;


    /**
     * 사용자 역할 열거형
     */
    public enum Role {
        ROLE_NOBSM, ROLE_BSM,ROLE_ADMIN
    }

    /**
     * 엔티티 생성 전 호출되는 메서드
     * 생성 일시를 현재 날짜로 설정합니다.
     */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDate.now();
        if (this.role == null) {
            this.role = Role.ROLE_NOBSM;
        }
        if (this.dollar == null) {
            this.dollar = 0.0;
        }
        if (this.won == null) {
            this.won = 0.0;
        }
        if (this.streakCount == null) {
            this.streakCount = 0L;
        }
        this.recentDate = LocalDate.now();
    }
}

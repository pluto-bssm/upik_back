package pluto.upik.shared.oauth2jwt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {

    @Id
    @Column(name = "user_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY) // (1) User와의 관계를 정의하고,
    @MapsId                           // (2) 이 관계를 통해 ID를 매핑하라고 지시!
    @JoinColumn(name = "user_id")
    private User user;                // (3) 바로 이 필드로부터 ID를 가져옴

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private Date expiryDate;

    @Builder
    public RefreshToken(User user, String token, String role, Date expiryDate) {
        this.id = user.getId();
        this.user = user;
        this.token = token;
        this.role = role;
        this.expiryDate = expiryDate;
    }

    public void updateToken(String token, Date expiryDate) {
        this.token = token;
        this.expiryDate = expiryDate;
    }
}
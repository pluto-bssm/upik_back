package pluto.upik.shared.oauth2jwt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {
    @Id
    private String userId;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private Date expiryDate;
}
package pluto.upik.domain.token.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    private String email;

    @Column(nullable = false, length = 1000)
    private String token;

    @Column(nullable = false)
    private Long expiration;
}
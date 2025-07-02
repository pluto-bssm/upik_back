package pluto.upik.domain.token.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pluto.upik.domain.token.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
}

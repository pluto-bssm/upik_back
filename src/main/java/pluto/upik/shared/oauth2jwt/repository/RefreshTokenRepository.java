package pluto.upik.shared.oauth2jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pluto.upik.shared.oauth2jwt.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    // userId로 리프레시 토큰 조회
    Optional<RefreshToken> findByUserId(String userId);

    // 토큰 값으로 리프레시 토큰 조회
    Optional<RefreshToken> findByToken(String token);

    // userId로 리프레시 토큰 삭제
    void deleteByUserId(String userId);

    // 토큰 값으로 리프레시 토큰 삭제
    void deleteByToken(String token);
}
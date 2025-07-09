package pluto.upik.shared.oauth2jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying; // 1. 임포트 추가
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pluto.upik.shared.oauth2jwt.entity.RefreshToken;
import pluto.upik.shared.oauth2jwt.entity.User;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    boolean existsByToken(String token);

    @Modifying
    @Transactional
    void deleteByToken(String token);

    Optional<RefreshToken> findByUser(User user);
}
package pluto.upik.shared.oauth2jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying; // 1. 임포트 추가
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pluto.upik.shared.oauth2jwt.entity.RefreshToken;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findByUserId(String userId);

    Optional<RefreshToken> findByToken(String token);

    boolean existsByToken(String token);

    @Modifying // 2. 이 어노테이션을 반드시 추가!
    @Transactional
    void deleteByToken(String token);

    @Modifying // 3. 여기도 일관성을 위해 추가하는 것을 권장
    @Transactional
    void deleteByUserId(String userId);
}
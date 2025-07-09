package pluto.upik.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pluto.upik.domain.user.data.model.User;

import java.util.Optional;
import java.util.UUID;

/**
 * 사용자 레포지토리
 * 사용자 엔티티에 대한 데이터베이스 접근을 제공합니다.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * 사용자명으로 사용자를 찾습니다.
     *
     * @param username 사용자명
     * @return 사용자 (존재하지 않을 경우 빈 Optional)
     */
    Optional<User> findByUsername(String username);

    /**
     * 사용자명으로 사용자가 존재하는지 확인합니다.
     *
     * @param username 사용자명
     * @return 존재 여부
     */
    boolean existsByUsername(String username);

    /**
     * 이메일로 사용자를 찾습니다.
     *
     * @param email 이메일
     * @return 사용자 (존재하지 않을 경우 빈 Optional)
     */
    Optional<User> findByEmail(String email);
}

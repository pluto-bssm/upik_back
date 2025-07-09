package pluto.upik.shared.oauth2jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pluto.upik.shared.oauth2jwt.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    // ★★★ 활성 사용자만 조회 (ROLE_DELETED 제외) ★★★
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.role != 'ROLE_DELETED'")
    Optional<User> findByUsername(@Param("username") String username);

    // ★★★ 삭제된 사용자 포함 모든 사용자 조회 ★★★
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsernameIncludingDeleted(@Param("username") String username);

    @Query("SELECT u.name FROM User u WHERE u.username = :username AND u.role != 'ROLE_DELETED'")
    Optional<String> findNameByUsername(@Param("username") String username);

    boolean existsByUsername(String username);
}
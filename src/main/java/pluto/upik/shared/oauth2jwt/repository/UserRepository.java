package pluto.upik.shared.oauth2jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pluto.upik.shared.oauth2jwt.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    // ★★★ 기본 사용자 조회 (삭제되지 않은 사용자만) ★★★
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.role != 'ROLE_DELETED'")
    Optional<User> findByUsername(@Param("username") String username);

    // ★★★ 삭제 여부 관계없이 조회 (관리자용) ★★★
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsernameIncludingDeleted(@Param("username") String username);

    // ★★★ name만 조회하는 별도 메서드 (삭제되지 않은 사용자만) ★★★
    @Query("SELECT u.name FROM User u WHERE u.username = :username AND u.role != 'ROLE_DELETED'")
    Optional<String> findNameByUsername(@Param("username") String username);

    // ★★★ 사용자 존재 여부 확인 (삭제되지 않은 사용자만) ★★★
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username AND u.role != 'ROLE_DELETED'")
    boolean existsByUsername(@Param("username") String username);
}
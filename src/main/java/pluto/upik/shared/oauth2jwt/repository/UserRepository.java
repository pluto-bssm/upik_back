package pluto.upik.shared.oauth2jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pluto.upik.shared.oauth2jwt.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    // ★★★ 기본 사용자 조회 ★★★
    Optional<User> findByUsername(String username);

    // ★★★ name만 조회하는 별도 메서드 (타입 에러 해결) ★★★
    @Query("SELECT u.name FROM User u WHERE u.username = :username")
    Optional<String> findNameByUsername(@Param("username") String username);

    // ★★★ 사용자 존재 여부 확인 ★★★
    boolean existsByUsername(String username);
}

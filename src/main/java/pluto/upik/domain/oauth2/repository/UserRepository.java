package pluto.upik.domain.oauth2.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pluto.upik.domain.oauth2.data.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.recentDate = CURRENT_TIMESTAMP WHERE u.email = :email")
    void updateRecentDate(@Param("email") String email);

}

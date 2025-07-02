package pluto.upik.shared.oauth2jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pluto.upik.shared.oauth2jwt.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
}
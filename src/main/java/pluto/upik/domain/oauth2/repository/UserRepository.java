package pluto.upik.domain.oauth2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pluto.upik.domain.oauth2.data.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
}

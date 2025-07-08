package pluto.upik.shared.oauth2jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pluto.upik.shared.oauth2jwt.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User>  findByUsername(String username);
}
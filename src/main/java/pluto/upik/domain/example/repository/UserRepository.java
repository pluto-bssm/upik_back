package pluto.upik.domain.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pluto.upik.shared.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
}

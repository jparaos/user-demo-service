package cl.jparaos.app.repository;

import cl.jparaos.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    User getUserByEmail(String email);
    User getUserByEmailAndToken(String email, String token);

}

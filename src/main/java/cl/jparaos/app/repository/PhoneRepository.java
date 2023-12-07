package cl.jparaos.app.repository;

import cl.jparaos.app.model.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneRepository extends JpaRepository<Phone, Long> {
}

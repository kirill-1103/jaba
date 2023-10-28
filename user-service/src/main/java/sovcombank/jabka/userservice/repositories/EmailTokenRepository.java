package sovcombank.jabka.userservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sovcombank.jabka.userservice.model.EmailToken;

@Repository
public interface EmailTokenRepository extends JpaRepository<EmailToken, Long> {
    EmailToken findByToken(String activationToken);
}

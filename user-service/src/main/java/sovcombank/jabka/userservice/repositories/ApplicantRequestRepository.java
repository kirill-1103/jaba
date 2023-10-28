package sovcombank.jabka.userservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sovcombank.jabka.userservice.model.ApplicantRequest;

import java.util.Optional;

public interface ApplicantRequestRepository extends JpaRepository<ApplicantRequest,Long> {
    Optional<ApplicantRequest> findByUserId(Long id);
}

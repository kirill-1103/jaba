package sovcombank.jabka.userservice.service.interfaces;

import org.springframework.transaction.annotation.Transactional;
import ru.sovcombank.openapi.model.ApplicantRequestOpenApi;
import sovcombank.jabka.userservice.model.ApplicantRequest;

import java.util.List;
import java.util.Optional;

public interface ApplicantRequestService {
    Optional<ApplicantRequest> add(ApplicantRequestOpenApi request);

    Optional<ApplicantRequest> findById(Long id);

    List<ApplicantRequest> getAll();

    Optional<ApplicantRequest> update(ApplicantRequestOpenApi requestOpenApi);

    void delete(Long id);


    @Transactional
    Optional<ApplicantRequest> findByUserId(Long id);
}

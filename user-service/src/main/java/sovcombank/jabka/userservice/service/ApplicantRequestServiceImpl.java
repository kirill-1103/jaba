package sovcombank.jabka.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sovcombank.openapi.model.ApplicantRequestOpenApi;
import ru.sovcombank.openapi.model.ERoleOpenApi;
import sovcombank.jabka.userservice.exception.BadRequestException;
import sovcombank.jabka.userservice.exception.NotFoundException;
import sovcombank.jabka.userservice.mapper.ApplicantRequestMapper;
import sovcombank.jabka.userservice.model.ApplicantRequest;
import sovcombank.jabka.userservice.model.Role;
import sovcombank.jabka.userservice.model.UserEntity;
import sovcombank.jabka.userservice.model.enums.ApplicantRequestStatus;
import sovcombank.jabka.userservice.repositories.ApplicantRequestRepository;
import sovcombank.jabka.userservice.repositories.UserRepository;
import sovcombank.jabka.userservice.service.interfaces.ApplicantRequestService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ApplicantRequestServiceImpl implements ApplicantRequestService {
    private final ApplicantRequestRepository applicantRequestRepository;
    private final ApplicantRequestMapper applicantRequestMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Optional<ApplicantRequest> add(ApplicantRequestOpenApi requestOpenApi) {
        requestOpenApi.setId(null);
        ApplicantRequest request = applicantRequestMapper.toApplicantRequest(requestOpenApi);
        if (Objects.isNull(request)) {
            throw new BadRequestException();
        }

        if(requestOpenApi.getUser() == null || requestOpenApi.getUser().getId() == null){
            throw new BadRequestException("User or User's Id is null");
        }

        if(applicantRequestRepository.findByUserId(requestOpenApi.getUser().getId()).isPresent()){
            throw new BadRequestException("Applicant Request with such User Id is already exists");
        }

        request.setRequestStatus(ApplicantRequestStatus.ON_MODERATION);

        ApplicantRequest requestFromDb = applicantRequestRepository.save(request);
        return Optional.of(requestFromDb);
    }

    @Override
    public Optional<ApplicantRequest> findById(Long id) {
        return applicantRequestRepository.findById(id);
    }


    @Override
    public List<ApplicantRequest> getAll() {
        return applicantRequestRepository.findAll();
    }

    @Override
    @Transactional
    public Optional<ApplicantRequest> update(ApplicantRequestOpenApi requestOpenApi) {
        if (Objects.isNull(requestOpenApi.getId())) {
            throw new BadRequestException("Id is null");
        }
        applicantRequestRepository.findById(requestOpenApi.getId())
                .orElseThrow(() -> new BadRequestException("Request with such id not exists"));
        ApplicantRequest request = applicantRequestMapper.toApplicantRequest(requestOpenApi);
        if (Objects.isNull(request)) {
            throw new BadRequestException();
        }
        if(request.getRequestStatus() == null){
            request.setRequestStatus(ApplicantRequestStatus.ON_MODERATION);
        }
        if(request.getRequestStatus().equals(ApplicantRequestStatus.APPROVED)) {
            UserEntity user = request.getUser();
            Set<Role> roles = user.getRoles();
            roles.removeIf(role -> role.getName().equals(ERoleOpenApi.ENROLLEE));
            roles.add(new Role(ERoleOpenApi.STUDENT));
            user.setRoles(roles);
            userRepository.save(user);
        }
        request = applicantRequestRepository.save(request);
        return Optional.of(request);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (Objects.isNull(id)) {
            throw new BadRequestException("Id is null");
        }
        ApplicantRequest request = applicantRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Request with such id is not exists"));
        applicantRequestRepository.delete(request);
    }

    @Override
    @Transactional
    public Optional<ApplicantRequest> findByUserId(Long id){
        if(Objects.isNull(id)){
            throw new BadRequestException("Id is null");
        }
        ApplicantRequest request = applicantRequestRepository.findByUserId(id)
                .orElseThrow(()->new NotFoundException(String.format("User with such id not found. Id:%d",id)));
        return Optional.of(request);
    }
}

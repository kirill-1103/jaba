package sovcombank.jabka.userservice.mapper;

import org.mapstruct.Mapper;
import ru.sovcombank.openapi.model.ApplicantRequestOpenApi;
import sovcombank.jabka.userservice.model.ApplicantRequest;

@Mapper(componentModel = "spring")

public interface RequestMapper {
    ApplicantRequest toRequest(ApplicantRequestOpenApi requestOpenApi);

    ApplicantRequestOpenApi toRequestOpenApi(ApplicantRequest request);
}

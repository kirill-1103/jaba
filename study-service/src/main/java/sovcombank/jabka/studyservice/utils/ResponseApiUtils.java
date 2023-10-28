package sovcombank.jabka.studyservice.utils;

import org.springframework.http.HttpStatus;
import ru.sovcombank.openapi.ApiResponse;

public class ResponseApiUtils {
    public static  boolean okResponse(ApiResponse<?> apiResponse) {
        return apiResponse.getStatusCode() < HttpStatus.MULTIPLE_CHOICES.value() && apiResponse.getStatusCode() >= HttpStatus.OK.value();
    }
}

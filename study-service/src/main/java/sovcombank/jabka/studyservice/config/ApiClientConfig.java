package sovcombank.jabka.studyservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sovcombank.openapi.ApiClient;
import ru.sovcombank.openapi.api.AuthorizationApi;
import ru.sovcombank.openapi.api.UserApi;
import sovcombank.jabka.libs.security.state.TokenKeeper;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ApiClientConfig {

    @Value("${spring.application.api-name}")
    private String accessApiName;

    private final TokenKeeper tokenKeeper;


    @Bean
    public ApiClient userClient() {
        ApiClient apiClient = ru.sovcombank.openapi.Configuration.getDefaultApiClient();


        apiClient.setRequestInterceptor((http) -> {
            http.setHeader(
                    "Authorization",
                    "Bearer "
                            + tokenKeeper.getToken(accessApiName, List.of("ROLE_ADMIN"))
            );
        });

        return apiClient;
    }

    @Bean
    public UserApi userApi(ApiClient apiClient) {
        UserApi userApi = new UserApi(apiClient);
        return userApi;
    }

    @Bean
    public AuthorizationApi authorizationApi(ApiClient apiClient){
        return new AuthorizationApi(apiClient);
    }


//    @Bean
//    public ClientHttpRequestInterceptor interceptor(){
//        return (request, body, execution) -> {
//            request.getHeaders().add("Authorization", "Bearer "+tokenKeeper.getToken(
//                appName, List.of("ROLE_ADMIN")
//            ));
//            return execution.execute(request,body);
//        };
//    }

}

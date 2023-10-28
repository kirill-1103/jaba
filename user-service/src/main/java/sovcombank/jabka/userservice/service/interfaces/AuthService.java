package sovcombank.jabka.userservice.service.interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sovcombank.openapi.model.JwtResponseOpenApi;
import ru.sovcombank.openapi.model.LoginRequestOpenApi;
import ru.sovcombank.openapi.model.UserOpenApi;

import sovcombank.jabka.userservice.model.UserEntity;
import sovcombank.jabka.libs.security.handler.AuthFailureHandler;

@Service
public interface AuthService {
    JwtResponseOpenApi createTokenAndAuthorized(LoginRequestOpenApi loginRequestOpenApi);

    JwtResponseOpenApi createAuthToken(LoginRequestOpenApi loginRequestOpenApi);

    JwtResponseOpenApi createAuthToken(UserOpenApi userOpenApi);

    @Transactional
    void sendVerificationEmail(UserEntity user);

    @Transactional
    ResponseEntity<Void> activateUser(String token);

    @Transactional
    ResponseEntity<Void> recoveryPassword(String password, String token);

    @Transactional
    ResponseEntity<Void> sendRecoveryPasswordMail(String email);
}

package sovcombank.jabka.libs.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import sovcombank.jabka.libs.security.utils.JsonUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class AuthFailureHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        Map<String, String> result = new HashMap<>();

        if (exception instanceof BadCredentialsException) {
            result.put("message", "Invalid credentials");
        } else if (exception instanceof InsufficientAuthenticationException) {
            result.put("message", "Invalid authentication request");
        } else {
            result.put("message", "Authentication failure");
        }
        JsonUtils.write(response.getWriter(), result);
        log.debug(String.format("Не удалось выполнить запрос {%s} (ошибка аутентификации). Ex: %s", request.getRequestURI(), exception.getMessage()));
    }
}

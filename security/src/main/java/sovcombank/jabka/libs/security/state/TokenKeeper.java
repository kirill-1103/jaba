package sovcombank.jabka.libs.security.state;

import org.springframework.stereotype.Component;
import sovcombank.jabka.libs.security.utils.JwtUtils;

import java.util.List;
import java.util.Objects;

@Component
public class TokenKeeper {
    private JwtUtils jwtUtils;

    private final Long timeForUpdateMs = 1000 * 60L * 10;

    private String token = null;

    public TokenKeeper(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    public String getToken(String serviceName, List<String> rolesList) {
        if (Objects.isNull(token) || jwtUtils.getTimeLeftMs(token) < timeForUpdateMs) {
            this.token = jwtUtils.generateToken(serviceName, rolesList);
        }
        return token;
    }

}

package sovcombank.jabka.libs.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String secret;


    @Value("${jwt.lifetime}")
    private Duration lifetime;

    private final static String CLAIM_ROLES = "roles";

    public String generateToken(String username, List<String> rolesList) {
        Map<String, Object> claims = new HashMap<>();

        claims.put(CLAIM_ROLES, rolesList);

        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + lifetime.toMillis());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String getUsername(String token) {
        return getAllClaimsByToken(token).getSubject();
    }

    public List<String> getRoles(String token) {
        return getAllClaimsByToken(token).get(CLAIM_ROLES, List.class);
    }

    public Long getTimeLeftMs(String token) {
        Claims claims = getAllClaimsByToken(token);
        return claims.getExpiration().getTime() - new Date().getTime();
    }

    private Claims getAllClaimsByToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}

package sovcombank.jabka.libs.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sovcombank.jabka.libs.security.utils.JwtUtils;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    private final static String HEADER_AUTH_NAME = "Authorization";
    private final static String HEADER_AUTH_STARTS_WITH = "Bearer ";
    private final static Integer HEADER_AUTH_OFFSET = 7;

    private final JwtUtils jwtUtils;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HEADER_AUTH_NAME);
        String username = null;
        String jwt = null;

//        log.debug("HEADER: "+authHeader);

        if (authHeader != null && authHeader.startsWith(HEADER_AUTH_STARTS_WITH)) {
            jwt = authHeader.substring(HEADER_AUTH_OFFSET);
            try {
                username = jwtUtils.getUsername(jwt);
            } catch (ExpiredJwtException e) {
                log.debug("Токен устарел");
            } catch (SignatureException e) {
                log.debug("Неверная подпись");
            }
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    jwtUtils.getRoles(jwt).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}

package sovcombank.jabka.libs.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sovcombank.jabka.libs.security.filter.JwtRequestFilter;
import sovcombank.jabka.libs.security.interfaces.PathUtils;
import sovcombank.jabka.libs.security.handler.AuthFailureHandler;
import sovcombank.jabka.libs.security.model.Role;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final PathUtils pathUtils;
    private final JwtRequestFilter jwtRequestFilter;
    private final AuthFailureHandler authFailureHandler;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        if (pathUtils.getPublic().length != 0) {
            http.authorizeHttpRequests(request -> request
                    .requestMatchers(pathUtils.getPublic())
                    .permitAll());
        }
        if (pathUtils.getForAuthorized().length != 0) {
            http.authorizeHttpRequests(request -> request
                    .requestMatchers(pathUtils.getForAuthorized())
                    .authenticated());
        }
        if (pathUtils.getForStudent().length != 0) {
            http.authorizeHttpRequests(request -> request
                    .requestMatchers(pathUtils.getForStudent())
                    .hasRole(Role.STUDENT.getValue().substring(5)));
        }
        if (pathUtils.getForAdmin().length != 0) {
            http.authorizeHttpRequests(request -> request
                    .requestMatchers(pathUtils.getForAdmin())
                    .hasRole(Role.ADMIN.getValue().substring(5)));
        }
        if (pathUtils.getForCommitte().length != 0) {
            http.authorizeHttpRequests(request -> request
                    .requestMatchers(pathUtils.getForCommitte())
                    .hasRole(Role.COMMITTE.getValue().substring(5)));
        }
        if (pathUtils.getForCurator().length != 0) {
            http.authorizeHttpRequests(request -> request
                    .requestMatchers(pathUtils.getForCurator())
                    .hasRole(Role.CURATOR.getValue().substring(5)));
        }
        if (pathUtils.getForEnrollee().length != 0) {
            http.authorizeHttpRequests(request -> request
                    .requestMatchers(pathUtils.getForEnrollee())
                    .hasRole(Role.ENROLLEE.getValue().substring(5)));
        }
        if (pathUtils.getForModerator().length != 0) {
            http.authorizeHttpRequests(request -> request
                    .requestMatchers(pathUtils.getForModerator())
                    .hasRole(Role.MODERATOR.getValue().substring(5)));
        }
        return http
                .formLogin(form -> form.disable())
                .sessionManagement(management -> management
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(authFailureHandler))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(csrf -> csrf.disable())
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}

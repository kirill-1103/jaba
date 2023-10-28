package sovcombank.jabka.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sovcombank.openapi.model.JwtResponseOpenApi;
import ru.sovcombank.openapi.model.LoginRequestOpenApi;
import ru.sovcombank.openapi.model.UserOpenApi;
import sovcombank.jabka.libs.security.utils.JwtUtils;
import sovcombank.jabka.userservice.exception.BadRequestException;
import sovcombank.jabka.userservice.mapper.UserMapper;

import sovcombank.jabka.userservice.model.EmailToken;
import sovcombank.jabka.userservice.model.UserEntity;
import sovcombank.jabka.userservice.model.enums.ActivationStatus;
import sovcombank.jabka.userservice.model.enums.TokenType;
import sovcombank.jabka.userservice.repositories.EmailTokenRepository;
import sovcombank.jabka.userservice.repositories.UserRepository;
import sovcombank.jabka.userservice.service.interfaces.AuthService;
import sovcombank.jabka.userservice.service.interfaces.UserService;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final MailSenderServiceImpl mailService;
    private final PasswordEncoder passwordEncoder;
    private final EmailTokenRepository emailTokenRepository;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public JwtResponseOpenApi createTokenAndAuthorized(LoginRequestOpenApi loginRequestOpenApi) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestOpenApi.getLogin(),
                    loginRequestOpenApi.getPassword()));
            log.debug(String.format("USER %s has logged in", loginRequestOpenApi.getLogin()));
        } catch (BadCredentialsException | InternalAuthenticationServiceException e) {
            throw new BadRequestException("Authentication error! Incorrect login/password input.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Authentication error!");
        }
        return createAuthToken(loginRequestOpenApi);
    }

    @Override
    public JwtResponseOpenApi createAuthToken(LoginRequestOpenApi loginRequestOpenApi) {
        return createAuthTokenByLogin(loginRequestOpenApi.getLogin());
    }

    @Override
    public JwtResponseOpenApi createAuthToken(UserOpenApi userOpenApi) {
        return createAuthTokenByLogin(userOpenApi.getLogin());
    }

    private JwtResponseOpenApi createAuthTokenByLogin(String login) {
        UserEntity user = userService.loadUserByUsername(login);
        List<String> debug = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        String token = jwtUtils.generateToken(user.getUsername(),
                user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        user.setPassword(null);
        return new JwtResponseOpenApi(token, userMapper.toUserOpenApi(user));
    }

    @Override
    @Transactional
    public void sendVerificationEmail(UserEntity user) {
        String userEmail = user.getEmail();
        EmailToken activationToken = new EmailToken(user);
        activationToken.setTokenType(TokenType.ACTIVATION);
        mailService.sendVerificationEmail(userEmail, activationToken);
        emailTokenRepository.save(activationToken);
    }

    @Override
    public ResponseEntity<Void> activateUser(String token) {
        EmailToken activationToken = emailTokenRepository.findByToken(token);
        if (Objects.isNull(activationToken)) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
        TokenType tokenType = activationToken.getTokenType();
        Date tokenExpirationDate = activationToken.getExpirationDate();
        Date currentDate = new Date();

        if (currentDate.before(tokenExpirationDate) && tokenType.equals(TokenType.ACTIVATION)) {
            UserEntity user = activationToken.getUserEntity();
            ActivationStatus activationStatus = user.getActivationStatus();
            if (!activationStatus.equals(ActivationStatus.ACTIVATED)) {
                Optional<UserEntity> userRepoOpt = userRepository.findById(user.getId());
                if (userRepoOpt.isPresent()) {
                    UserEntity userRepo = userRepoOpt.get();
                    userRepo.setActivationStatus(ActivationStatus.ACTIVATED);
                    userRepository.save(userRepo);
                    emailTokenRepository.delete(activationToken);
                    return ResponseEntity
                            .ok()
                            .build();
                }
                return ResponseEntity
                        .notFound()
                        .build();
            }
        }
        return ResponseEntity
                .badRequest()
                .build();
    }

    @Override
    public ResponseEntity<Void> recoveryPassword(String password, String token) {
        EmailToken recoveryToken = emailTokenRepository.findByToken(token);
        if (!Objects.isNull(recoveryToken)) {
            TokenType tokenType = recoveryToken.getTokenType();
            if (tokenType.equals(TokenType.PASSWORD_RECOVERY)) {
                Date tokenExpirationDate = recoveryToken.getExpirationDate();
                Date currentDate = new Date();

                if (currentDate.before(tokenExpirationDate) && currentDate.equals(tokenExpirationDate)) {
                    UserEntity user = recoveryToken.getUserEntity();
                    Optional<UserEntity> userRepoOpt = userRepository.findById(user.getId());

                    if (userRepoOpt.isPresent()) {
                        UserEntity userRepo = userRepoOpt.get();
                        userRepo.setPassword(passwordEncoder.encode(password));
                        userRepository.save(userRepo);
                        emailTokenRepository.delete(recoveryToken);
                        return ResponseEntity
                                .ok()
                                .build();
                    }
                }
            }
        }
        return ResponseEntity
                .notFound()
                .build();
    }

    @Override
    public ResponseEntity<Void> sendRecoveryPasswordMail(String email) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            EmailToken recoveryToken = new EmailToken(user);
            recoveryToken.setTokenType(TokenType.PASSWORD_RECOVERY);
            mailService.sendRecoveryPassword(email, recoveryToken);
            emailTokenRepository.save(recoveryToken);
            return ResponseEntity
                    .ok()
                    .build();
        }
        return ResponseEntity
                .notFound()
                .build();
    }
}

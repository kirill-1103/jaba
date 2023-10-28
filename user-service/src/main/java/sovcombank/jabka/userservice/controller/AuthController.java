package sovcombank.jabka.userservice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import ru.sovcombank.openapi.api.AuthorizationApiDelegate;
import ru.sovcombank.openapi.model.JwtResponseOpenApi;
import ru.sovcombank.openapi.model.LoginRequestOpenApi;
import ru.sovcombank.openapi.model.SignupRequestOpenApi;
import ru.sovcombank.openapi.model.UserOpenApi;
import sovcombank.jabka.libs.security.handler.AuthFailureHandler;
import sovcombank.jabka.userservice.exception.ForbiddenException;

import sovcombank.jabka.userservice.model.UserEntity;



import sovcombank.jabka.userservice.service.interfaces.AuthService;
import sovcombank.jabka.userservice.service.interfaces.UserService;
import sovcombank.jabka.libs.security.filter.JwtRequestFilter;
@RestController
@RequestMapping("/api/user/auth")
@RequiredArgsConstructor
public class AuthController implements AuthorizationApiDelegate {
    private final AuthService authService;
    private final UserService userService;
    private static final String MAPPING_AUTH = "/signin";
    private static final String MAPPING_UPDATE_TOKEN = "/update-token";
    private static final String MAPPING_REGISTRATION = "/signup";
    private static final String MAPPING_ACTIVATION = "/activation";
    private static final String MAPPING_FORGET = "/forget";
    private static final String MAPPING_RECOVERY = "/recovery";

    @Override
    @PostMapping(MAPPING_AUTH)
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<JwtResponseOpenApi> authenticateUser(@RequestBody LoginRequestOpenApi loginRequestOpenApi) {
        JwtResponseOpenApi jwtResponseOpenApi = authService.createTokenAndAuthorized(loginRequestOpenApi);
        return ResponseEntity.ok(jwtResponseOpenApi);
    }

    @Override
    @PostMapping(MAPPING_REGISTRATION)
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<Void> registerUser(@RequestBody SignupRequestOpenApi signupRequestOpenApi) {

        UserEntity user = userService.saveOrUpdate(signupRequestOpenApi);
        authService.sendVerificationEmail(user);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @GetMapping(MAPPING_UPDATE_TOKEN)
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<JwtResponseOpenApi> updateUserToken(@RequestBody UserOpenApi userOpenApi) {
        String authenticatedName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!authenticatedName.equals(userOpenApi.getLogin())) {
            throw new ForbiddenException("User can't update token for other user!");
        }
        return ResponseEntity.ok(authService.createAuthToken(userOpenApi));
    }

    @Override
    @GetMapping(MAPPING_ACTIVATION)
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<Void> activateUser(@RequestParam(name = "token") String token) {
        return authService.activateUser(token);
    }

    @Override
    @Transactional
    @PutMapping(MAPPING_RECOVERY)
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<Void> recoveryPassword(@RequestBody String body,
                                                 @RequestParam(name = "token") String token) {
        return authService.recoveryPassword(body, token);
    }

    @Override
    @Transactional
    @PutMapping(MAPPING_FORGET)
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<Void> sendRecoveryPasswordMail(@RequestBody String body) {
        return authService.sendRecoveryPasswordMail(body);
    }
}

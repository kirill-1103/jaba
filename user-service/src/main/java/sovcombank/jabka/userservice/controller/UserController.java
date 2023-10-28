package sovcombank.jabka.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.sovcombank.openapi.api.UserApiDelegate;
import ru.sovcombank.openapi.model.JwtResponseOpenApi;
import ru.sovcombank.openapi.model.UpdateUserOpenApi;
import ru.sovcombank.openapi.model.UserOpenApi;
import sovcombank.jabka.userservice.mapper.UserMapper;
import sovcombank.jabka.userservice.service.interfaces.AuthService;
import sovcombank.jabka.userservice.service.interfaces.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController implements UserApiDelegate {
    private final UserService userService;
    private final UserMapper userMapper;

    private final static String MAPPING_GET_ONE = "/{id}";

    private final static String MAPPING_DELETE_ONE = "/{id}";


    private final static String MAPPING_GET_BY_GROUP = "/group";

    private final AuthService authService;

    @Override
    @DeleteMapping(MAPPING_DELETE_ONE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<List<UserOpenApi>> getAllUsers() {
        return ResponseEntity.ok(userService.getAll()
                .stream()
                .map(userMapper::toUserOpenApi)
                .peek((userOpenApi -> userOpenApi.setPassword(null)))
                .collect(Collectors.toList()));
    }

    @Override
    @GetMapping(MAPPING_GET_BY_GROUP)
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<List<UserOpenApi>> getUsersByGroupNumber(@RequestBody String groupNumber) {
        String groupName = groupNumber.replaceAll("\"", "");
        return ResponseEntity.ok(userMapper.toListOpenApi(userService.getUsersByGroupNumber(groupName)));
    }

    @Override
    @GetMapping(MAPPING_GET_ONE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('MODERATOR') or hasRole('TEACHER') " +
            "or hasRole('CURATOR') or hasRole('COMMITTE') or hasRole('ENROLLEE')")
    public ResponseEntity<UserOpenApi> showUserInfo(@PathVariable  Long id) {
        UserOpenApi userOpenApi = userMapper.toUserOpenApi(userService.getUserById(id));
        userOpenApi.setPassword(null);
        return ResponseEntity.ok(userOpenApi);
    }

    @Override
    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('MODERATOR') or hasRole('TEACHER') " +
            "or hasRole('CURATOR') or hasRole('COMMITTE') or hasRole('ENROLLEE')")
    public ResponseEntity<JwtResponseOpenApi> updateUser(@RequestBody  UpdateUserOpenApi updateUserOpenApi) {
        userService.update(updateUserOpenApi);
        JwtResponseOpenApi token = authService.createAuthToken(updateUserOpenApi.getNewUser());
        updateUserOpenApi.getNewUser().setPassword(null);
        return ResponseEntity.ok(new JwtResponseOpenApi(token.getAccessToken(),updateUserOpenApi.getNewUser()));
    }

    @Override
    @PostMapping("/users/update")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> updateUsers(@RequestBody  List<UserOpenApi> usersOpenApi) {
        userService.updateUsers(usersOpenApi);
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/ids")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<List<UserOpenApi>> getUsersByIds(@RequestBody List<Long> ids) {
        return ResponseEntity.ok(
                userMapper.toListOpenApi(userService.getUsersByIds(ids))
        );
    }

}

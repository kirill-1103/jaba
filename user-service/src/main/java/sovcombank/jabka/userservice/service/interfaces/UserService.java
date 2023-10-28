package sovcombank.jabka.userservice.service.interfaces;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;
import ru.sovcombank.openapi.model.SignupRequestOpenApi;
import ru.sovcombank.openapi.model.UpdateUserOpenApi;
import ru.sovcombank.openapi.model.UserOpenApi;
import sovcombank.jabka.userservice.model.UserEntity;

import java.util.List;

public interface UserService extends UserDetailsService {
    List<UserEntity> getAll();

    @Override
    UserEntity loadUserByUsername(String login);

    @Transactional
    UserEntity saveOrUpdate(SignupRequestOpenApi signupRequestOpenApi);

    @Transactional
    UserEntity saveOrUpdate(UserOpenApi userOpenApi);

    @Transactional
    void deleteUser(Long id);

    UserEntity getUserById(Long id);

    void update(UpdateUserOpenApi updateUserOpenApi);

    List<UserEntity>getUsersByIds(List<Long> ids);


    List<UserEntity> getUsersByGroupNumber(String groupNumber);

    void updateUsers(List<UserOpenApi> usersOpenApi);
}

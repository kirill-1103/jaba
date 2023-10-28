package sovcombank.jabka.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import ru.sovcombank.openapi.model.ERoleOpenApi;
import ru.sovcombank.openapi.model.SignupRequestOpenApi;
import ru.sovcombank.openapi.model.UpdateUserOpenApi;
import ru.sovcombank.openapi.model.UserOpenApi;
import sovcombank.jabka.userservice.exception.BadRequestException;
import sovcombank.jabka.userservice.exception.NotFoundException;
import sovcombank.jabka.userservice.exception.StateException;
import sovcombank.jabka.userservice.mapper.UserMapper;
import sovcombank.jabka.userservice.model.Role;
import sovcombank.jabka.userservice.model.UserEntity;
import sovcombank.jabka.userservice.model.enums.ActivationStatus;
import sovcombank.jabka.userservice.repositories.RoleRepository;
import sovcombank.jabka.userservice.repositories.UserRepository;
import sovcombank.jabka.userservice.service.interfaces.UserService;

//import sovcombank.jabka.libs.security.filter.JwtRequestFilter

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    @Override
    public UserEntity loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByLogin(username)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }

    @Override
    public List<UserEntity> getAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional

    public UserEntity saveOrUpdate(SignupRequestOpenApi signupRequestOpenApi) {
        UserEntity user = userMapper.toUser(signupRequestOpenApi);
        user.setActivationStatus(ActivationStatus.WAIT_FOR_EMAIL_VERIFICATION);
        return saveOrUpdate(user);
    }

    @Override
    @Transactional
    public UserEntity saveOrUpdate(UserOpenApi userOpenApi) {
        UserEntity user = userMapper.toUser(userOpenApi);
        return saveOrUpdate(user);
    }


    private UserEntity saveOrUpdate(UserEntity user) {
        if (Objects.nonNull(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (Objects.isNull(user.getId())) {
            loginOrEmailExistsCheck(user);
        }
        setRole(user);
        return userRepository.save(user);
    }

    @Override
    @Transactional

    public void deleteUser(Long id) {
        userRepository.findById(id)
                .ifPresentOrElse(
                        userRepository::delete,
                        () -> {
                            throw new NotFoundException(String.format("User with id %d Not Found!", id));
                        });
    }

    @Override
    public UserEntity getUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    @Transactional
    public void update(UpdateUserOpenApi updateUserOpenApi) {

        if (Objects.isNull(updateUserOpenApi.getNewUser()) || Objects.isNull(updateUserOpenApi.getOldUser())) {
            throw new BadRequestException("Old or new user is null");
        }
        if (Objects.isNull(updateUserOpenApi.getOldUser().getId())) {
            throw new BadRequestException("Id is null");
        }
        UserOpenApi oldUser = updateUserOpenApi.getOldUser();
        UserOpenApi newUser = updateUserOpenApi.getNewUser();
        newUser.setId(oldUser.getId());


        UserEntity userFromDb = userRepository.findById(newUser.getId())
                .orElseThrow(() -> new BadRequestException(String.format("User with such id is not exists. Id: %d", newUser.getId())));

        if (!newUser.getEmail().equals(oldUser.getEmail())) {
            emailExistsCheck(newUser.getEmail());
        }
        if (!newUser.getLogin().equals(oldUser.getLogin())) {
            loginExistsCheck(newUser.getLogin());
        }
        UserEntity userEntity = userMapper.toUser(newUser);
        newUser.setPassword(null);
        if(userEntity.getPassword() != null && !userFromDb.getPassword().equals(userEntity.getPassword())){
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        }
        userRepository.save(userEntity);
    }

    @Override
    public List<UserEntity> getUsersByIds(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    @Override
    public List<UserEntity> getUsersByGroupNumber(String groupNumber) {
        List<UserEntity> users  = userRepository.findByGroup(groupNumber);
        return users;
    }

    @Override
    public void updateUsers(List<UserOpenApi> usersOpenApi) {
        usersOpenApi.forEach(user->{
            if(user.getId() == null){
                throw new BadRequestException();
            }
        });
        List<UserEntity> usersFromDb = userRepository.findByIdIn(usersOpenApi.stream().
                map(UserOpenApi::getId)
                .toList()
        ).stream().toList();
        if(usersFromDb.size() != usersOpenApi.size()){
            throw new NotFoundException("Some users not found");
        }
        List<UserEntity> usersForSave = userMapper.toListUserEntity(usersOpenApi);
        userRepository.saveAll(usersForSave);
    }

    private void loginOrEmailExistsCheck(UserEntity user) {
        loginExistsCheck(user.getLogin());
        emailExistsCheck(user.getEmail());
    }


    private void loginExistsCheck(String login) {
        userRepository.findByLogin(login)
                .ifPresent((u) -> {
                    throw new StateException("User with such login is already exists");
                });
    }

    private void emailExistsCheck(String email) {
        userRepository.findByEmail(email)
                .ifPresent((u) -> {
                    throw new StateException("User with such email is already exists");
                });
    }

    private void setRole(UserEntity user) {
        roleRepository.findByName(ERoleOpenApi.ENROLLEE).ifPresentOrElse(
                (role) -> user.setRoles(Set.of(role)),
                () -> user.setRoles(Set.of(new Role(ERoleOpenApi.ENROLLEE)))
        );
    }
}

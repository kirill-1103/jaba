package sovcombank.jabka.userservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sovcombank.jabka.userservice.model.UserEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {

    Optional<UserEntity> findByLogin(String login);

    Optional<UserEntity> findById(Long id);

    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findByIdIn(List<Long> ids);

    List<UserEntity> findByGroup(String groupNumber);
}

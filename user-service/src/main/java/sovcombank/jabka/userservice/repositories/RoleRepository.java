package sovcombank.jabka.userservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sovcombank.openapi.model.ERoleOpenApi;
import sovcombank.jabka.userservice.model.Role;

import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role,Long> {

    Optional<Role> findByName(ERoleOpenApi name);
}

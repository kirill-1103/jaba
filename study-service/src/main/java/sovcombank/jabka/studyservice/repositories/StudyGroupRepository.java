package sovcombank.jabka.studyservice.repositories;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sovcombank.jabka.studyservice.models.StudyGroup;

import java.util.List;

@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {
    boolean existsByName(String name);
    boolean existsById(@NonNull Long id);

}

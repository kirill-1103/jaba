package sovcombank.jabka.studyservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sovcombank.jabka.studyservice.models.Subject;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByStudyGroupId(Long groupId);

    List<Subject> findByCreatorId(Long creatorId);

    List<Subject> findByEditorsIds_ProfessorId(Long editorId);

}

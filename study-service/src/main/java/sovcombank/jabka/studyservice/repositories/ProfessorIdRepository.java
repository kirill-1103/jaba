package sovcombank.jabka.studyservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sovcombank.jabka.studyservice.models.ProfessorIdTable;

@Repository
public interface ProfessorIdRepository extends JpaRepository<ProfessorIdTable,Long> {
}

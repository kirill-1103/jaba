package sovcombank.jabka.studyservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sovcombank.jabka.studyservice.models.StudyMaterials;

@Repository
public interface StudyMaterialsRepository extends JpaRepository<StudyMaterials, Long> {
}

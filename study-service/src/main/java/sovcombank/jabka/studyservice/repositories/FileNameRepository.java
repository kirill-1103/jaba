package sovcombank.jabka.studyservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sovcombank.jabka.studyservice.models.FileName;

@Repository
public interface FileNameRepository extends JpaRepository<FileName, Long> {

}

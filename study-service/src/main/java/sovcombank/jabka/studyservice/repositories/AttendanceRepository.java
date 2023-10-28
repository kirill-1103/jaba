package sovcombank.jabka.studyservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sovcombank.jabka.studyservice.models.AttendanceStatistics;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceStatistics,Long> {
    List<AttendanceStatistics> findByStudentIdIn(List<Long> ids);
}

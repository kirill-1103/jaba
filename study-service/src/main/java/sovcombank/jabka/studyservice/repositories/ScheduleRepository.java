package sovcombank.jabka.studyservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sovcombank.jabka.studyservice.models.Schedule;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByProfessorProfessorId(Long id);

    List<Schedule> findByGroupId(Long id);
}

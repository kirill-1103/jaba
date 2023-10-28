package sovcombank.jabka.studyservice.services.interfaces;

import org.springframework.stereotype.Service;
import ru.sovcombank.openapi.model.ScheduleOpenAPI;
import sovcombank.jabka.studyservice.models.Schedule;

import java.util.List;

@Service
public interface ScheduleService {
    void createSchedule(ScheduleOpenAPI openAPI);

    void deleteScheduleById(Long id);

    List<Schedule> getAll();

    List<Schedule> getByGroupId(Long id);

    List<Schedule> getByProfessorId(Long professorId);

    void updateSchedule(ScheduleOpenAPI scheduleOpenAPI);
}

package sovcombank.jabka.studyservice.services.interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.sovcombank.openapi.model.AttendanceStatisticsOpenApi;
import sovcombank.jabka.studyservice.models.AttendanceStatistics;

import java.util.List;

@Service
public interface AttendanceStatisticService {
    ResponseEntity<Void> saveStatistics(List<AttendanceStatisticsOpenApi> attendanceStatisticsOpenApi);

    List<AttendanceStatistics> getStatisticsByGroupId(Long groupId);
    List<AttendanceStatistics> getStatisticsBySchedule(Long scheduleId);

    AttendanceStatistics getById(Long id);

    List<AttendanceStatistics> getStatisticsBySubjectId(Long subjectId);
}

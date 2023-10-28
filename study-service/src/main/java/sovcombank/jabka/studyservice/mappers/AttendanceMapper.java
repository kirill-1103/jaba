package sovcombank.jabka.studyservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.sovcombank.openapi.model.AttendanceStatisticsOpenApi;
import sovcombank.jabka.studyservice.models.AttendanceStatistics;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ScheduleMapper.class,  ScheduleIdMapper.class})
public interface AttendanceMapper {
    @Mapping(target = "status", source = "attendanceStatus")
    @Mapping(target = "scheduleId", source = "schedule")
    AttendanceStatisticsOpenApi toOpenApi(AttendanceStatistics attendanceStatistics);


    @Mapping(target = "attendanceStatus", source = "status")
    @Mapping(target = "schedule", source = "scheduleId")
    AttendanceStatistics toAttendanceStatistics(AttendanceStatisticsOpenApi attendanceStatisticsOpenApi);

    @Mapping(target = "status", source = "attendanceStatus")
    @Mapping(target = "scheduleId", source = "schedule")
    List<AttendanceStatisticsOpenApi> toOpenApiList(List<AttendanceStatistics> list);


    @Mapping(target = "attendanceStatus", source = "status")
    @Mapping(target = "schedule", source = "scheduleId")
    List<AttendanceStatistics> toListAttendanceStatistics(List<AttendanceStatisticsOpenApi> list);
}

package sovcombank.jabka.studyservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.sovcombank.openapi.model.ScheduleOpenAPI;
import sovcombank.jabka.studyservice.models.ProfessorIdTable;
import sovcombank.jabka.studyservice.models.Schedule;
import sovcombank.jabka.studyservice.models.Subject;

import java.util.List;

@Mapper(componentModel = "spring", uses = {SubjectMapper.class, DateMapper.class, SubjectIdMapperImpl.class, StudyGroupIdMapper.class})
public interface ScheduleMapper {

    @Mapping(target = "professor", expression = "java(professorFromId(scheduleOpenAPI.getProfessorId()))")
    @Mapping(target = "dateTime", source = "date", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "group", source = "studyGroupId")
    Schedule toSchedule(ScheduleOpenAPI scheduleOpenAPI);

    @Mapping(target = "professor", expression = "java(professorFromId(scheduleOpenAPI.getProfessorId()))")
    @Mapping(target = "dateTime", source = "date", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "group", source = "studyGroupId")
    List<Schedule> toSchedule(List<ScheduleOpenAPI> scheduleOpenAPI);


    @Mapping(target = "professorId", expression = "java(professorToId(schedule.getProfessor()))")
    @Mapping(target = "date", source = "dateTime", dateFormat = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @Mapping(target = "studyGroupId", source = "group")
    @Mapping(target = "subjectId", source = "subject")
    ScheduleOpenAPI toOpenAPI(Schedule schedule);

    @Mapping(target = "professorId", expression = "java(professorToId(schedule.getProfessor()))")
    @Mapping(target = "date", source = "dateTime", dateFormat = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @Mapping(target = "studyGroupId", source = "group")
    List<ScheduleOpenAPI> toOpenAPI(List<Schedule> schedule);

    default ProfessorIdTable professorFromId (Long id){
        return new ProfessorIdTable(id);
    }

    default Long professorToId (ProfessorIdTable professor){
        return professor.getProfessorId();
    }

}

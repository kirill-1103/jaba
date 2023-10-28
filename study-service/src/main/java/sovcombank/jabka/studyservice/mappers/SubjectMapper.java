package sovcombank.jabka.studyservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.ApplicationContext;
import ru.sovcombank.openapi.model.SubjectOpenAPI;
import sovcombank.jabka.studyservice.models.ProfessorIdTable;
import sovcombank.jabka.studyservice.models.Schedule;
import sovcombank.jabka.studyservice.models.StudyGroup;
import sovcombank.jabka.studyservice.models.Subject;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {StudyMaterialsIdMapper.class})
public interface SubjectMapper {

    @Mapping(target = "editorsIds", expression = "java(idsToProfessorIds(subjectOpenAPI.getEditorsIds()))")
    @Mapping(target = "studyMaterials", source = "studyMaterialsIds")
    Subject toSubject(SubjectOpenAPI subjectOpenAPI);

    @Mapping(target = "editorsIds", expression = "java(idsToProfessorIds(subjectOpenAPI.getEditorsIds()))")
    @Mapping(target = "studyMaterials", source = "studyMaterialsIds")
    List<Subject> toSubject(List<SubjectOpenAPI> subjectOpenAPI);

    @Mapping(target = "editorsIds", expression = "java(professorsToEditorsIds(subject.getEditorsIds()))")
    @Mapping(target = "studyGroupsIds", expression = "java(groupsToIds(subject.getStudyGroup()))")
    @Mapping(target = "scheduleIds", expression = "java(schedulesToIds(subject.getSchedule()))")
    @Mapping(target = "studyMaterialsIds", source = "studyMaterials")
    SubjectOpenAPI toOpenAPI(Subject subject);

    @Mapping(target = "editorsIds", expression = "java(professorsToEditorsIds(subject.getEditorsIds()))")
    @Mapping(target = "studyGroupsIds", expression = "java(groupsToIds(subject.getStudyGroup()))")
    @Mapping(target = "scheduleIds", expression = "java(schedulesToIds(subject.getSchedule()))")
    @Mapping(target = "studyMaterialsIds", source = "studyMaterials")
    List<SubjectOpenAPI> toOpenAPI(List<Subject> subject);

    default List<Long> groupsToIds(Set<StudyGroup> studyGroups){
        return studyGroups.stream().map(StudyGroup::getId).toList();
    }

    default List<Long> professorsToEditorsIds(Set<ProfessorIdTable> professorIds){
        return professorIds.stream().map(ProfessorIdTable::getProfessorId).toList();
    }

    default Set<ProfessorIdTable> idsToProfessorIds(List<Long> ids){
        return ids.stream().map(id->{
            ProfessorIdTable professorIdTable = new ProfessorIdTable();
            professorIdTable.setProfessorId(id);
            return professorIdTable;
        }).collect(Collectors.toSet());
    }

    default List<Long> schedulesToIds(Set<Schedule> schedules){
        return schedules.stream().map(Schedule::getId).toList();
    }
}

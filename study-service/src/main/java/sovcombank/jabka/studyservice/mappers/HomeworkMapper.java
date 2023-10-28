package sovcombank.jabka.studyservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.sovcombank.openapi.model.HomeworkOpenAPI;
import sovcombank.jabka.studyservice.models.Homework;
import sovcombank.jabka.studyservice.models.StudyMaterials;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

@Mapper(componentModel = "spring", uses = {DateMapper.class, SubjectMapper.class})
public interface HomeworkMapper {
    @Mapping(target = "date", source = "date", dateFormat = "yyyy-MM-dd HH:mm:ss")
    Homework toHomework(HomeworkOpenAPI homeworkOpenAPI);

    @Mapping(target = "date", source = "date", dateFormat = "yyyy-MM-dd HH:mm:ss")
    List<Homework> toHomework(List<HomeworkOpenAPI> homeworkOpenAPI);

    @Mapping(target = "date", source = "date", dateFormat = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @Mapping(target = "taskId", expression = "java(homework.getTask().getId())")
    @Mapping(target = "fileIds", expression = "java(homework.getFileNames().stream().map(f->f.getId()).toList())")
    @Mapping(target = "filesNames", expression = "java(homework.getFileNames().stream().map(f->f.getInitialName()).toList())")
    HomeworkOpenAPI toOpenAPI(Homework homework);

    @Mapping(target = "date", source = "date", dateFormat = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @Mapping(target = "taskId", expression = "java(homework.getTask().getId())")
    @Mapping(target = "fileIds", expression = "java(homework.getFileNames().stream().map(f->f.getId()).toList())")
    @Mapping(target = "filesNames", expression = "java(homework.getFileNames().stream().map(f->f.getInitialName()).toList())")
    List<HomeworkOpenAPI> toOpenAPI(List<Homework> homework);
}

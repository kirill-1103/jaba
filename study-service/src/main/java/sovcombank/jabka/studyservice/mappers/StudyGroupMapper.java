package sovcombank.jabka.studyservice.mappers;

import org.mapstruct.Mapper;
import ru.sovcombank.openapi.model.StudyGroupOpenAPI;
import sovcombank.jabka.studyservice.models.StudyGroup;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudyGroupMapper {
    StudyGroup toStudyGroup(StudyGroupOpenAPI studyGroupOpenAPI);

    StudyGroupOpenAPI toOpenAPI(StudyGroup studyGroup);

    List<StudyGroup> toStudyGroup(List<StudyGroupOpenAPI> studyGroupOpenAPI);

    List<StudyGroupOpenAPI> toOpenAPI(List<StudyGroup> studyGroup);
}

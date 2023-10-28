package sovcombank.jabka.studyservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import sovcombank.jabka.studyservice.exceptions.NotFoundException;
import sovcombank.jabka.studyservice.models.Subject;
import sovcombank.jabka.studyservice.repositories.SubjectRepository;

@Mapper(componentModel = "spring")
public class SubjectIdMapper {
    @Autowired
    private SubjectRepository subjectRepository;

    public Subject getSubjectById(Long subjectId) {
        if (subjectId == null) {
            return null;
        }
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new NotFoundException(String.format("Subject with id %d wasn't found", subjectId)));
    }

    public Long getIdBySubject(Subject subject){
        return subject.getId();
    }
}

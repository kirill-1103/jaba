package sovcombank.jabka.studyservice.services.interfaces;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.sovcombank.openapi.model.SubjectOpenAPI;

import java.util.List;

@Service
public interface SubjectService {
    ResponseEntity<Void> createSubject(SubjectOpenAPI subjectOpenAPI);
    ResponseEntity<Void> deleteSubject(Long id);
    ResponseEntity<List<SubjectOpenAPI>> getAllSubjects();
    ResponseEntity<SubjectOpenAPI> getSubjectById(Long id);
    ResponseEntity<Void> updateSubject(SubjectOpenAPI subjectOpenAPI);

    ResponseEntity<List<SubjectOpenAPI>> getSubjectsByCreatorId(Long creatorId);

    ResponseEntity<List<SubjectOpenAPI>> getSubjectsByEditorId(Long editorId);

    ResponseEntity<List<SubjectOpenAPI>> getSubjectsByGroupId(Long groupId);
}

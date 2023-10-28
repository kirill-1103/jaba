package sovcombank.jabka.studyservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import ru.sovcombank.openapi.api.SubjectApiDelegate;
import ru.sovcombank.openapi.model.SubjectOpenAPI;
import sovcombank.jabka.studyservice.models.Subject;
import sovcombank.jabka.studyservice.services.interfaces.SubjectService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/study/subject")
@RequiredArgsConstructor
public class SubjectController implements SubjectApiDelegate {
    private final SubjectService subjectService;
    @PostMapping
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> createSubject(@RequestBody SubjectOpenAPI subjectOpenAPI) {
        return subjectService.createSubject(subjectOpenAPI);
    }

    @DeleteMapping("/{id}")
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> deleteSubject(@Valid @PathVariable(name = "id") Long id) {
        return subjectService.deleteSubject(id);
    }

    @GetMapping
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<List<SubjectOpenAPI>> getAllSubjects() {
        return subjectService.getAllSubjects();
    }

    @GetMapping("/{id}")
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('TEACHER') " +
            "or hasRole('STUDENT') or hasRole('CURATOR')")
    public ResponseEntity<SubjectOpenAPI> getSubjectById(@Valid @PathVariable(name = "id")Long id) {
        return subjectService.getSubjectById(id);
    }

    @PutMapping
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> updateSubject(@RequestBody SubjectOpenAPI subjectOpenAPI) {
        return subjectService.updateSubject(subjectOpenAPI);
    }


    @Override
    @GetMapping("/creator/{creator_id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<List<SubjectOpenAPI>> getSubjectsByCreatorId(@PathVariable(value = "creator_id")  Long creatorId) {
        return subjectService.getSubjectsByCreatorId(creatorId);
    }

    @Override
    @GetMapping("/editor/{editor_id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<List<SubjectOpenAPI>> getSubjectsByEditor(@PathVariable(value = "editor_id") Long editorId) {
        return subjectService.getSubjectsByEditorId(editorId);
    }

    @Override
    @GetMapping("/group/{group_id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<List<SubjectOpenAPI>> getSubjectsByGroupId(@PathVariable(value = "group_id") Long groupId) {
        return subjectService.getSubjectsByGroupId(groupId);
    }
}

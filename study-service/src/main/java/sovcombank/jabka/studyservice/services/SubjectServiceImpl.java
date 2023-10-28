package sovcombank.jabka.studyservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sovcombank.openapi.model.SubjectOpenAPI;
import sovcombank.jabka.studyservice.exceptions.NotFoundException;
import sovcombank.jabka.studyservice.mappers.SubjectMapper;
import sovcombank.jabka.studyservice.models.ProfessorIdTable;
import sovcombank.jabka.studyservice.models.StudyMaterials;
import sovcombank.jabka.studyservice.models.Subject;
import sovcombank.jabka.studyservice.repositories.ProfessorIdRepository;
import sovcombank.jabka.studyservice.repositories.StudyGroupRepository;
import sovcombank.jabka.studyservice.repositories.SubjectRepository;
import sovcombank.jabka.studyservice.services.interfaces.SubjectService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {
    //todo: проверять везде, что создатель и едиторы существуют через UserAPI
    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;

    private final ProfessorIdRepository professorIdRepository;

    private final StudyGroupRepository studyGroupRepository;

    @Transactional
    @Override
    public ResponseEntity<Void> createSubject(SubjectOpenAPI subjectOpenAPI) {
        Subject subject = subjectMapper.toSubject(subjectOpenAPI);
        subjectOpenAPI.getEditorsIds().forEach(editorId -> {
            if (professorIdRepository.findById(editorId).isEmpty()) {
                professorIdRepository.save(new ProfessorIdTable(editorId));
            }
        });
        subject.setStudyGroup(subjectOpenAPI.getStudyGroupsIds().stream().map(groupId -> studyGroupRepository.findById(groupId)
                .orElseThrow(()
                        -> new NotFoundException(String.format("Group with such id not found. Id:%d", groupId))
                )).collect(Collectors.toSet()));

        subjectRepository.save(subject);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @Transactional
    @Override
    public ResponseEntity<Void> deleteSubject(Long id) {
        Optional<Subject> subjectOpt = subjectRepository.findById(id);
        if (subjectOpt.isEmpty()) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
        subjectRepository.deleteById(id);
        return ResponseEntity
                .ok()
                .build();
    }

    @Transactional
    @Override
    public ResponseEntity<List<SubjectOpenAPI>> getAllSubjects() {
        List<Subject> subjects = subjectRepository.findAll();
        return ResponseEntity.ok(subjects
                .stream()
                .map(this::fromSubject)
                .collect(Collectors.toList())
        );
    }

    @Transactional
    @Override
    public ResponseEntity<SubjectOpenAPI> getSubjectById(Long id) {
        Optional<Subject> subjectOpt = subjectRepository.findById(id);
//        subjectOpt.get().getSchedule().stream().findFirst().get();
        return subjectOpt
                .map(subject -> ResponseEntity.ok(this.fromSubject(subject)))
                .orElseGet
                        (
                                () -> ResponseEntity.notFound().build()
                        );
    }

    @Transactional
    @Override
    public ResponseEntity<Void> updateSubject(SubjectOpenAPI subjectOpenAPI) {
        Optional<Subject> subjectOpt = subjectRepository.findById(subjectOpenAPI.getId());
        if (subjectOpt.isEmpty()) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
        subjectOpenAPI.getEditorsIds().forEach(editorId -> {
            if (professorIdRepository.findById(editorId).isEmpty()) {
                professorIdRepository.save(new ProfessorIdTable(editorId));
            }
        });

        Subject updatedSubject = subjectMapper.toSubject(subjectOpenAPI);
        updatedSubject.setStudyGroup(subjectOpenAPI.getStudyGroupsIds().stream().map(groupId -> studyGroupRepository.findById(groupId)
                .orElseThrow(()
                        -> new NotFoundException(String.format("Group with such id not found. Id:%d", groupId))
                )).collect(Collectors.toSet()));
        subjectRepository.save(updatedSubject);
        return ResponseEntity
                .ok()
                .build();
    }

    @Override
    public ResponseEntity<List<SubjectOpenAPI>> getSubjectsByCreatorId(Long creatorId) {
        List<Subject> subjects = subjectRepository.findByCreatorId(creatorId);
        return ResponseEntity.ok(subjects
                .stream()
                .map(this::fromSubject)
                .collect(Collectors.toList())
        );
    }

    @Override
    public ResponseEntity<List<SubjectOpenAPI>> getSubjectsByEditorId(Long editorId) {
        List<Subject> subjects = subjectRepository.findByEditorsIds_ProfessorId(editorId);
        return ResponseEntity.ok(subjects
                .stream()
                .map(this::fromSubject)
                .collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<List<SubjectOpenAPI>> getSubjectsByGroupId(Long groupId) {
        List<Subject> subjects = subjectRepository.findByStudyGroupId(groupId);
        return ResponseEntity.ok(subjects
                .stream()
                .map(this::fromSubject)
                .collect(Collectors.toList()));
    }

    private SubjectOpenAPI fromSubject(Subject subject) {
        SubjectOpenAPI subjectOpenAPI = subjectMapper.toOpenAPI(subject);
        subjectOpenAPI.setStudyMaterialsIds(
                subject.getStudyMaterials().stream().map(StudyMaterials::getId).toList()
        );
        return subjectOpenAPI;
    }
}

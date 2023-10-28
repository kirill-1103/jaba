package sovcombank.jabka.studyservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.sovcombank.openapi.api.MaterialsApiDelegate;
import ru.sovcombank.openapi.model.StudyMaterialsOpenAPI;
import ru.sovcombank.openapi.model.StudyMaterialsType;
import sovcombank.jabka.studyservice.mappers.StudyMaterialsMapper;
import sovcombank.jabka.studyservice.models.FileName;
import sovcombank.jabka.studyservice.models.StudyMaterials;
import sovcombank.jabka.studyservice.services.interfaces.HomeworkService;
import sovcombank.jabka.studyservice.services.interfaces.StudyMaterialsService;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/study/materials")
@RequiredArgsConstructor
public class StudyMaterialsController implements MaterialsApiDelegate {
    private final StudyMaterialsService studyMaterialsService;
    private final StudyMaterialsMapper materialsMapper;
    private final HomeworkService homeworkService;

    @PostMapping
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('TEACHER')")
    public ResponseEntity<Void> createMaterials(@RequestPart("studyMaterials") StudyMaterialsOpenAPI studyMaterialsOpenAPI,
                                                @RequestPart("files") List<MultipartFile> files
    ) {
        return studyMaterialsService.createMaterials(studyMaterialsOpenAPI, files);
    }

    @DeleteMapping("/{id}")
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('TEACHER')")
    public ResponseEntity<Void> deleteMaterials(@Valid @PathVariable(name = "id") Long id) {
        return studyMaterialsService.deleteMaterials(id);
    }

    @GetMapping
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('TEACHER')")
    public ResponseEntity<List<StudyMaterialsOpenAPI>> getAllMaterials() {
        List<StudyMaterials> studyMaterials = studyMaterialsService.getAllMaterials();
        List<StudyMaterialsOpenAPI> studyMaterialsOpenAPI = toMaterialsOpenApi(studyMaterials);
        studyMaterialsOpenAPI
                .stream()
                .filter(materials -> materials.getType().equals(StudyMaterialsType.TASK))
                .forEach(materials -> materials.setHomeworkIds(homeworkService.getIdsByTaskId(materials.getId())));
        return ResponseEntity
                .ok(studyMaterialsOpenAPI);
    }

    @GetMapping("/{id}")
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('TEACHER') " +
            "or hasRole('STUDENT') or hasRole('CURATOR')")
    public ResponseEntity<StudyMaterialsOpenAPI> getMaterialsById(
            @Valid @PathVariable(name = "id") Long id
    ) {
        StudyMaterials studyMaterials = studyMaterialsService.getMaterialsById(id);
        return ResponseEntity.ok(toMaterialsOpenApi(studyMaterials));
    }

    @PutMapping
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('TEACHER')")
    public ResponseEntity<Void> updateMaterials(@RequestPart("studyMaterials") StudyMaterialsOpenAPI studyMaterialsOpenApi,
                                                @RequestPart("files") List<MultipartFile> files
    ) {
        return studyMaterialsService.updateMaterials(studyMaterialsOpenApi, files);
    }

    @GetMapping("/subject/{subjectId}")
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('TEACHER') " +
            "or hasRole('STUDENT') or hasRole('CURATOR')")
    public ResponseEntity<List<StudyMaterialsOpenAPI>> getMaterialsBySubjectId(
            @Valid @PathVariable(name = "subjectId") Long subjectId
    ) {
        List<StudyMaterials> materials = studyMaterialsService.getMaterialsBySubjectId(subjectId);
        return ResponseEntity.ok(toMaterialsOpenApi(materials));
    }

    private List<StudyMaterialsOpenAPI> toMaterialsOpenApi(List<StudyMaterials> materials) {
        List<StudyMaterialsOpenAPI> materialsOpenAPI = materialsMapper.toOpenAPI(materials);
        Iterator<StudyMaterials> iteratorMaterials = materials.listIterator();
        Iterator<StudyMaterialsOpenAPI> iteratorMaterialsOpenApi = materialsOpenAPI.listIterator();
        while (iteratorMaterials.hasNext()) {
            StudyMaterials studyMaterials = iteratorMaterials.next();
            StudyMaterialsOpenAPI studyMaterialsOpenAPI = iteratorMaterialsOpenApi.next();
            studyMaterialsOpenAPI.setFilesId(getIdsFileNames(studyMaterials.getAttachedFiles()));
            studyMaterialsOpenAPI.setFilesNames(getStringFileNames(studyMaterials.getAttachedFiles()));
        }
        return materialsOpenAPI;
    }

    private StudyMaterialsOpenAPI toMaterialsOpenApi(StudyMaterials materials) {
        StudyMaterialsOpenAPI materialsOpenAPI = materialsMapper.toOpenAPI(materials);
        materialsOpenAPI.setFilesId(getIdsFileNames(materials.getAttachedFiles()));
        materialsOpenAPI.setFilesNames(getStringFileNames(materials.getAttachedFiles()));
        return materialsOpenAPI;
    }

    private List<String> getStringFileNames(Set<FileName> fileNames) {
        return fileNames.stream()
                .map(FileName::getNameS3)
                .collect(Collectors.toList());
    }

    private List<Long> getIdsFileNames(Set<FileName> fileNames) {
        return fileNames.stream()
                .map(FileName::getId)
                .collect(Collectors.toList());
    }

}

package sovcombank.jabka.studyservice.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.sovcombank.openapi.api.HomeworkApiDelegate;
import ru.sovcombank.openapi.model.GradeHomeworkRequestOpenApi;
import ru.sovcombank.openapi.model.HomeworkOpenAPI;
import sovcombank.jabka.studyservice.mappers.HomeworkMapper;
import sovcombank.jabka.studyservice.models.FileName;
import sovcombank.jabka.studyservice.models.Homework;
import sovcombank.jabka.studyservice.services.interfaces.HomeworkService;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/study/homework")
@RequiredArgsConstructor
public class HomeworkController implements HomeworkApiDelegate {

    private final HomeworkService homeworkService;
    private final HomeworkMapper homeworkMapper;

    @PostMapping("/materials/{materialsId}")
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('TEACHER')")
    public ResponseEntity<Void> createHomework(
            @Valid @PathVariable(name = "materialsId") Long materialsId,
            @RequestPart("homework") HomeworkOpenAPI homework,
           List<MultipartFile> files
    ) {
        homeworkService.createHomework(materialsId, homework, files);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{homeworkId}")
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('TEACHER')")
    public ResponseEntity<Void> deleteHomework(
            @Valid @PathVariable(name = "homeworkId") Long homeworkId
    ) {
        homeworkService.deleteHomework(homeworkId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/materials/{materialsId}/all")
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('TEACHER')")
    public ResponseEntity<List<HomeworkOpenAPI>> getAllHomeworksByMaterials(
            @Valid @PathVariable(name = "materialsId") Long materialsId
    ) {
        List<Homework> homeworks = homeworkService.getAllHomeworkByMaterialsId(materialsId);
        return ResponseEntity.ok(this.homeworksOpenApiByHomeworks(homeworks));
    }

    @Override
    @PutMapping("/grade/{homeworkId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('TEACHER')")
    public ResponseEntity<Void> gradeHomework(@PathVariable Long homeworkId,@RequestBody GradeHomeworkRequestOpenApi gradeHomeworkRequestOpenApi) {
        homeworkService.grade(homeworkId, gradeHomeworkRequestOpenApi);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{homework_id}")
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('MODERATOR') or hasRole('TEACHER')" +
            "or hasRole('CURATOR')")
    public ResponseEntity<HomeworkOpenAPI> getHomeworkById(
            @Valid @PathVariable("homework_id") Long homeworkId
    ) {
        Homework homework = homeworkService.getHomeworkById(homeworkId);
        HomeworkOpenAPI homeworkOpenAPI = homeworkMapper.toOpenAPI(homework);
        homeworkOpenAPI.setFilesNames(getStringFileNames(homework.getFileNames()));
        return ResponseEntity.ok(homeworkOpenAPI);
    }

    @GetMapping("/materials/{materialsId}/student/{studentId}")
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('TEACHER') " +
            "or hasRole('STUDENT') or hasRole('CURATOR')")
    public ResponseEntity<HomeworkOpenAPI> getHomeworkByStudentAndMaterials(
            @Valid @PathVariable(name = "materialsId") Long materialsId,
            @Valid @PathVariable(name = "studentId") Long studentId
    ) {
        Homework homework = homeworkService.getHomeworkByStudentAndMaterials(materialsId, studentId);
        HomeworkOpenAPI homeworkOpenAPI = homeworkMapper.toOpenAPI(homework);
        homeworkOpenAPI.setFilesNames(getStringFileNames(homework.getFileNames()));
        return ResponseEntity.ok(homeworkOpenAPI);
    }

    @GetMapping("/student/{studentId}")
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('TEACHER') " +
            "or hasRole('STUDENT') or hasRole('CURATOR')")
    public ResponseEntity<List<HomeworkOpenAPI>> getHomeworkByStudentId(
            @Valid @PathVariable(name = "studentId") Long studentId
    ) {
        List<Homework> homeworks = homeworkService.getHomeworksByStudentId(studentId);
        return ResponseEntity.ok(this.homeworksOpenApiByHomeworks(homeworks));
    }

    @PutMapping("/{homework_id}")
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('TEACHER') " +
            "or hasRole('STUDENT') or hasRole('CURATOR')")
    public ResponseEntity<Void> updateHomework(
            @Valid @PathVariable(name = "homework_id") Long homeworkId,
            @RequestPart("homework") HomeworkOpenAPI homework,
            List<MultipartFile> files
    ) {
        homeworkService.updateHomework(homework, files);
        return ResponseEntity.ok().build();
    }

    private List<String> getStringFileNames(Set<FileName> fileNames) {
        return fileNames.stream()
                .map(FileName::getInitialName)
                .collect(Collectors.toList());
    }

    private List<Long> getIdsFileNames(Set<FileName> fileNames){
        return fileNames.stream()
                .map(FileName::getId)
                .collect(Collectors.toList());
    }

    private List<HomeworkOpenAPI> homeworksOpenApiByHomeworks(List<Homework> homeworks) {
        List<HomeworkOpenAPI> homeworksOpenAPI = homeworkMapper.toOpenAPI(homeworks);
        Iterator<Homework> iteratorHomework = homeworks.listIterator();
        Iterator<HomeworkOpenAPI> iteratorHomeworkOpenApi = homeworksOpenAPI.listIterator();
        while (iteratorHomework.hasNext()) {
            Homework homework = iteratorHomework.next();
            HomeworkOpenAPI homeworkOpenAPI = iteratorHomeworkOpenApi.next();
            homeworkOpenAPI.setFilesNames(getStringFileNames(homework.getFileNames()));
            homeworkOpenAPI.setFileIds(getIdsFileNames(homework.getFileNames()));
        }
        return homeworksOpenAPI;
    }
}

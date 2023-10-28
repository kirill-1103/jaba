package sovcombank.jabka.studyservice.services.interfaces;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.sovcombank.openapi.model.GradeHomeworkRequestOpenApi;
import ru.sovcombank.openapi.model.HomeworkOpenAPI;
import sovcombank.jabka.studyservice.models.Homework;

import java.util.List;

@Service
public interface HomeworkService {
    void createHomework(Long materialsId,
                        HomeworkOpenAPI homework,
                        List<MultipartFile> files);


    void deleteHomework(Long homeworkId);

    List<Homework> getAllHomeworkByMaterialsId(Long materialsId);

    Homework getHomeworkById(Long homeworkId);

    Homework getHomeworkByStudentAndMaterials(Long materialsId, Long studentId);

    List<Homework> getHomeworksByStudentId(Long studentId);

    void updateHomework(HomeworkOpenAPI homework, List<MultipartFile> file);

    void grade(Long homeworkId, GradeHomeworkRequestOpenApi gradeHomeworkRequestOpenApi);

    List<Homework> getByTaskId(Long taskId);
    List<Long> getIdsByTaskId(Long taskId);
}

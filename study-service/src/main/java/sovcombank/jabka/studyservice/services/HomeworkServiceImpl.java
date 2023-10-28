package sovcombank.jabka.studyservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.sovcombank.openapi.model.GradeHomeworkRequestOpenApi;
import ru.sovcombank.openapi.model.HomeworkOpenAPI;
import ru.sovcombank.openapi.model.UserOpenApi;
import ru.sovcombank.openapi.ApiException;
import ru.sovcombank.openapi.ApiResponse;
import ru.sovcombank.openapi.api.UserApi;
import sovcombank.jabka.studyservice.exceptions.BadRequestException;
import sovcombank.jabka.studyservice.exceptions.NotFoundException;
import sovcombank.jabka.studyservice.mappers.HomeworkMapper;
import sovcombank.jabka.studyservice.models.FileName;
import sovcombank.jabka.studyservice.models.Homework;
import sovcombank.jabka.studyservice.models.StudyMaterials;
import sovcombank.jabka.studyservice.repositories.HomeworkRepository;
import sovcombank.jabka.studyservice.repositories.StudyMaterialsRepository;
import sovcombank.jabka.studyservice.services.interfaces.FileNameService;
import sovcombank.jabka.studyservice.services.interfaces.FileService;
import sovcombank.jabka.studyservice.services.interfaces.HomeworkService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static sovcombank.jabka.studyservice.utils.ResponseApiUtils.okResponse;

@Service
@RequiredArgsConstructor
public class HomeworkServiceImpl implements HomeworkService {
    private final HomeworkRepository homeworkRepository;
    private final StudyMaterialsRepository studyMaterialsRepository;

    private final HomeworkMapper homeworkMapper;
    private final FileService fileService;

    private final FileNameService fileNameService;

    private final UserApi userApi;

    @Override
    @Transactional
    public void createHomework(Long materialsId, HomeworkOpenAPI homeworkOpenApi, List<MultipartFile> files) {
        if (files.isEmpty()) {
            throw new BadRequestException("Files is empty");
        }
        if(homeworkOpenApi.getStudentId() == null){
            throw new BadRequestException("Student id is null");
        }
        StudyMaterials studyMaterials =
                studyMaterialsRepository.findById(materialsId).orElseThrow(() ->
                        new BadRequestException(String.format("Materials with id %d not found", materialsId)));
        try {
            ApiResponse<UserOpenApi> userOpenApiApiResponse = userApi.showUserInfoWithHttpInfo(homeworkOpenApi.getStudentId());
            if (!okResponse(userOpenApiApiResponse)){
                if(userOpenApiApiResponse.getStatusCode() == HttpStatus.NOT_FOUND.value()){
                    throw new NotFoundException("User with such id not found");
                }else{
                    throw new BadRequestException("Failed load user");
                }
            }
        } catch (ApiException e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }

        Homework homework = homeworkMapper.toHomework(homeworkOpenApi);
        homework.setTask(studyMaterials);


        setHomeworkFileNamesAndSaveFiles(files, homework);
        homeworkRepository.save(homework);
    }

    @Override
    @Transactional
    public void deleteHomework(Long homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new NotFoundException(String.format("Homework with id %d not found", homeworkId)));
        homework.getFileNames()
                .forEach((fileName) -> {
                    fileService.removeFileByPath(getFilePath(fileName));
                });
        homeworkRepository.delete(homework);
    }

    @Override
    @Transactional
    public List<Homework> getAllHomeworkByMaterialsId(Long materialsId) {
        StudyMaterials studyMaterials = studyMaterialsRepository.findById(materialsId)
                .orElseThrow(() -> new NotFoundException(String.format("Materials with id %d not found", materialsId)));
        return studyMaterials.getHomeworks().stream().toList();
    }

    @Override
    @Transactional
    public Homework getHomeworkById(Long id) {
        return homeworkRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Homework with id %d not found", id)));
    }

    @Override
    @Transactional
    public Homework getHomeworkByStudentAndMaterials(Long materialsId, Long studentId) {
        ApiResponse<UserOpenApi> userOpenApiResponse = null;
        try {
            userOpenApiResponse = userApi.showUserInfoWithHttpInfo(studentId);
            if (!okResponse(userOpenApiResponse)){
                if(userOpenApiResponse.getStatusCode() == HttpStatus.NOT_FOUND.value()){
                    throw new NotFoundException("User with such id not found");
                }else{
                    throw new BadRequestException("Failed load user");
                }
            }
        } catch (ApiException e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }

        StudyMaterials studyMaterials = studyMaterialsRepository.findById(materialsId)
                .orElseThrow(() -> new NotFoundException(String.format("Materials with id %d not found", materialsId)));
        return studyMaterials.getHomeworks().stream().filter(homework -> homework.getStudentId().equals(studentId)).findAny()
                .orElseThrow(() -> new NotFoundException(String.format("Homework by student with id {%d} in materials with id {%d} not found.", studentId, materialsId)));
    }

    @Override
    @Transactional
    public List<Homework> getHomeworksByStudentId(Long studentId) {

        Integer statusCode;
        try {
            statusCode = userApi.showUserInfoWithHttpInfo(studentId).getStatusCode();
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        if (statusCode < 200 || statusCode > 300) {
            throw new BadRequestException(String.format("Student with id %d not found", studentId));
        }
        return homeworkRepository.findByStudentId(studentId);
    }

    @Override
    @Transactional
    public void updateHomework(HomeworkOpenAPI homeworkOpenAPI, List<MultipartFile> files) {
        if (homeworkOpenAPI.getId() == null) {
            throw new BadRequestException("Homework id is null");
        }
        Homework homework = this.getHomeworkById(homeworkOpenAPI.getId());

        homework.getFileNames().forEach((fileName) -> {
            fileService.removeFileByPath(getFilePath(fileName));
        });
        setHomeworkFileNamesAndSaveFiles(files, homework);
        homeworkRepository.save(homework);
    }

    @Override
    @Transactional
    public void grade(Long homeworkId, GradeHomeworkRequestOpenApi gradeHomeworkRequestOpenApi) {
        Homework homework = this.getHomeworkById(homeworkId);
        homework.setGrade(gradeHomeworkRequestOpenApi.getGrade().longValue());
        homework.setComment(gradeHomeworkRequestOpenApi.getComment());
        homeworkRepository.save(homework);
    }
    @Override
    @Transactional
    public List<Homework> getByTaskId(Long taskId){
        return homeworkRepository.findByTaskId(taskId);
    }

    @Override
    public List<Long> getIdsByTaskId(Long taskId) {
        return getByTaskId(taskId)
                .stream()
                .map(hw -> hw.getId())
                .collect(Collectors.toList());
    }

    private String getFilePath(FileName fileName) {
        return String.format("%s%s", FileService.HOMEWORK_PREFIX, fileName.getNameS3());
    }

    private void setHomeworkFileNamesAndSaveFiles(List<MultipartFile> files, Homework homework) {
        if (files.isEmpty()) {
            throw new BadRequestException("Files is empty");
        }
        Set<FileName> fileNames = files.stream()
                .map((file) -> {
                            FileName fileName = FileName.builder()
                                    .initialName(file.getOriginalFilename())
                                    .nameS3(fileService.generateHomeworkFileName(file.getOriginalFilename(), homework.getStudentId()))
                                    .homework(homework)
                                    .build();
                            fileService.save(getFilePath(fileName), file);
                            return fileName;
                        }
                )
                .map(fileNameService::saveFileName)
                .collect(Collectors.toSet());
        homework.setFileNames(fileNames);
    }
}

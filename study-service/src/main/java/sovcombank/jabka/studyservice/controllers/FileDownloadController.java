package sovcombank.jabka.studyservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sovcombank.openapi.api.FileDownloadApiDelegate;
import sovcombank.jabka.studyservice.exceptions.NotFoundException;
import sovcombank.jabka.studyservice.models.FileName;
import sovcombank.jabka.studyservice.models.StudyMaterials;
import sovcombank.jabka.studyservice.repositories.StudyMaterialsRepository;
import sovcombank.jabka.studyservice.services.interfaces.FileNameService;
import sovcombank.jabka.studyservice.services.interfaces.FileService;

@RestController
@RequestMapping("/api/study")
@RequiredArgsConstructor
public class FileDownloadController implements FileDownloadApiDelegate {
    private final FileService fileService;
    private final FileNameService fileNameService;
    private final StudyMaterialsRepository materialsRepository;

    @Override
    @GetMapping("/download/homework/{fileId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('MODERATOR') or hasRole('TEACHER') " +
            "or hasRole('CURATOR')")
    public ResponseEntity<Resource> downloadHomeworkFileById(@PathVariable Long fileId) {
        String name = fileNameService.getNameByFileId(fileId);
        ResponseEntity<ByteArrayResource> responseEntity =
                fileService.getFileByPath(getHomeworkFilePath(name));

        return getFile(responseEntity);
    }

    @Override
    @GetMapping("/download/materials/{fileId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('MODERATOR') or hasRole('TEACHER') " +
            "or hasRole('CURATOR') or hasRole('COMMITTE')")
    public ResponseEntity<Resource> downloadMaterialsFileById(@PathVariable Long fileId) {
        String name = fileNameService.getNameByFileId(fileId);
        ResponseEntity<ByteArrayResource> responseEntity =
                fileService.getFileByPath(getMaterialsFilePath(name));

        return getFile(responseEntity);
    }

    private String getHomeworkFilePath(String fileName) {
        return String.format("%s%s", FileService.HOMEWORK_PREFIX, fileName);
    }

    private String getMaterialsFilePath(String fileName) {
        return String.format("%s%s",
                FileService.MATERIALS_PREFIX,
                fileName);
    }

    private String getMaterialsFilePath(FileName fileName, StudyMaterials studyMaterials) {
        return String.format("%s%s/%s/%s",
                studyMaterials.getSubject().getId(),
                FileService.MATERIALS_PREFIX,
                studyMaterials.getId(),
                fileName.getNameS3());
    }

    private ResponseEntity<Resource> getFile(ResponseEntity<ByteArrayResource> responseEntity){
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            ByteArrayResource byteArrayResource = responseEntity.getBody();
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(responseEntity.getHeaders());

            Resource resource = new ByteArrayResource(byteArrayResource.getByteArray());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(headers)
                    .body(resource);
        } else {
            return ResponseEntity
                    .status(responseEntity.getStatusCode())
                    .headers(responseEntity.getHeaders())
                    .build();
        }
    }
}

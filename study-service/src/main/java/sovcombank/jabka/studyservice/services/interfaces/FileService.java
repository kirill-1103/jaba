package sovcombank.jabka.studyservice.services.interfaces;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import sovcombank.jabka.studyservice.models.enums.StudyMaterialsType;

public interface FileService {
    String MATERIALS_PREFIX = "materials/";

    String HOMEWORK_PREFIX = "homework/";

    String TEST_PREFIX = "test/";

    ResponseEntity<ByteArrayResource> getFileByPath(String path);

    void save(String path, MultipartFile multipartFile);

    void save(String path, Resource resource);

    void removeFileByPath(String path);

    String generateMaterialsFileName(String fileName, StudyMaterialsType materialsType);

    String generateHomeworkFileName(String fileName, Long studentId);

}

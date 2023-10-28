package sovcombank.jabka.studyservice.services.interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.sovcombank.openapi.model.StudyMaterialsOpenAPI;
import sovcombank.jabka.studyservice.models.StudyMaterials;

import java.util.List;

@Service
public interface StudyMaterialsService {
    ResponseEntity<Void> createMaterials(StudyMaterialsOpenAPI studyMaterialsOpenAPI, List<MultipartFile> files);
    ResponseEntity<Void> deleteMaterials(Long id);
    List<StudyMaterials> getAllMaterials();
    StudyMaterials getMaterialsById(Long id);
    ResponseEntity<Void> updateMaterials(StudyMaterialsOpenAPI studyMaterialsOpenAPI, List<MultipartFile> files);
    List<StudyMaterials> getMaterialsBySubjectId(Long subjectId);
}

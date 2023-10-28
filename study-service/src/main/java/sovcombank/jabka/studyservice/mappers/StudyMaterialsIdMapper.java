package sovcombank.jabka.studyservice.mappers;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import sovcombank.jabka.studyservice.exceptions.NotFoundException;
import sovcombank.jabka.studyservice.models.StudyMaterials;
import sovcombank.jabka.studyservice.models.Subject;
import sovcombank.jabka.studyservice.repositories.StudyMaterialsRepository;
import sovcombank.jabka.studyservice.repositories.SubjectRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public class StudyMaterialsIdMapper {
    @Autowired
    private StudyMaterialsRepository materialsRepository;
    @Autowired
    private SubjectRepository subjectRepository;

    public Set<StudyMaterials> getStudyMaterialsById(List<Long> studyMaterialsId){
        if (studyMaterialsId == null) {
            return null;
        }
        List<StudyMaterials> studyMaterials = materialsRepository.findAllById(studyMaterialsId);
        if(studyMaterials.size() < studyMaterialsId.size()){
            throw new NotFoundException("One or More Materials Weren't Found by Id in DB");
        }
        return new HashSet<>(studyMaterials);
    }

    public Long getIdByStudyMaterials(StudyMaterials studyMaterials){
        return studyMaterials.getId();
    }

    public List<Long> getMaterialsIdsBySubject (Subject subject){
        Set<StudyMaterials> studyMaterials = subject.getStudyMaterials();
        List<Long> ids = new ArrayList<>();
        studyMaterials.forEach(materials -> ids.add(materials.getId()));
        return ids;
    }
}

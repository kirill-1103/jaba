package sovcombank.jabka.studyservice.mappers;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import sovcombank.jabka.studyservice.exceptions.NotFoundException;
import sovcombank.jabka.studyservice.models.StudyGroup;
import sovcombank.jabka.studyservice.repositories.StudyGroupRepository;

@Mapper(componentModel = "spring")
public class StudyGroupIdMapper {
    @Autowired
    private StudyGroupRepository groupRepository;

    public StudyGroup getGroupById(Long studyGroupId) {
        if (studyGroupId == null) {
            return null;
        }
        return groupRepository.findById(studyGroupId)
                .orElseThrow(() -> new NotFoundException(String.format("Group with id %d wasn't found", studyGroupId)));
    }

    public Long getIdByGroup(StudyGroup group){
        return group.getId();
    }
}

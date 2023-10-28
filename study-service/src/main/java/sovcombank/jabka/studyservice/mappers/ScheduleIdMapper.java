package sovcombank.jabka.studyservice.mappers;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sovcombank.jabka.studyservice.exceptions.NotFoundException;
import sovcombank.jabka.studyservice.models.Schedule;
import sovcombank.jabka.studyservice.models.StudyGroup;
import sovcombank.jabka.studyservice.repositories.ScheduleRepository;

@Mapper(componentModel = "spring")
public class ScheduleIdMapper {

    @Autowired
    private  ScheduleRepository scheduleRepository;

    public Schedule getScheduleById(Long scheduleId) {
        if (scheduleId == null) {
            return null;
        }
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException(String.format("Schedule with id %d wasn't found", scheduleId)));
    }

    public Long getId(Schedule schedule){
        if(schedule == null){
            return null;
        }
        return schedule.getId();
    }
}

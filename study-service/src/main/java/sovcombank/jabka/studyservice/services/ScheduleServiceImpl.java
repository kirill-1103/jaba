package sovcombank.jabka.studyservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sovcombank.openapi.ApiException;
import ru.sovcombank.openapi.ApiResponse;
import ru.sovcombank.openapi.api.UserApi;
import ru.sovcombank.openapi.model.ScheduleOpenAPI;
import ru.sovcombank.openapi.model.UserOpenApi;
import sovcombank.jabka.studyservice.exceptions.BadRequestException;
import sovcombank.jabka.studyservice.exceptions.NotFoundException;
import sovcombank.jabka.studyservice.mappers.ScheduleMapper;
import sovcombank.jabka.studyservice.models.Schedule;
import sovcombank.jabka.studyservice.models.Subject;
import sovcombank.jabka.studyservice.repositories.ScheduleRepository;
import sovcombank.jabka.studyservice.repositories.StudyGroupRepository;
import sovcombank.jabka.studyservice.repositories.SubjectRepository;
import sovcombank.jabka.studyservice.services.interfaces.ScheduleService;

import static sovcombank.jabka.studyservice.utils.ResponseApiUtils.okResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;
    private final StudyGroupRepository groupRepository;
    private final SubjectRepository subjectRepository;
    private final UserApi userApi;

    @Override
    @Transactional
    public void createSchedule(ScheduleOpenAPI openAPI) {
        openAPI.setId(null);
        checkForUpdate(openAPI);
        try {
            Schedule schedule = scheduleMapper.toSchedule(openAPI);
            scheduleRepository.save(schedule);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteScheduleById(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Schedule with id %d not found", id)));
        scheduleRepository.delete(schedule);
    }

    @Override
    @Transactional
    public List<Schedule> getAll() {
        List<Schedule> schedules = scheduleRepository.findAll();
        return schedules;
    }

    @Override
    @Transactional
    public List<Schedule> getByGroupId(Long id) {
        groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Group with id %d not found", id)));
        List<Schedule> schedules = scheduleRepository.findByGroupId(id);
        return schedules;
    }

    @Override
    @Transactional
    public List<Schedule> getByProfessorId(Long professorId) {
        try {
            ApiResponse<UserOpenApi> usersResponse = userApi.showUserInfoWithHttpInfo(professorId);
            if (!okResponse(usersResponse)) {
                if (usersResponse.getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                    throw new NotFoundException(String.format("Professor with id %d not found", professorId));
                }
                throw new BadRequestException("Get Schedule by professor failed");
            }
        } catch (ApiException e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
        return scheduleRepository.findByProfessorProfessorId(professorId);
    }

    @Override
    @Transactional
    public void updateSchedule(ScheduleOpenAPI scheduleOpenAPI) {
        if (scheduleOpenAPI.getId() == null) {
            throw new BadRequestException("Id is null");
        }
        checkForUpdate(scheduleOpenAPI);
        scheduleRepository.findById(scheduleOpenAPI.getId())
                .orElseThrow(() -> new NotFoundException("Schedule with such id is not exists"));
        try {
            scheduleRepository.save(scheduleMapper.toSchedule(scheduleOpenAPI));
        } catch (Exception e) {
            throw new BadRequestException("Cannot update Schedule. Exception: " + e.getMessage());
        }
    }

    private void checkForUpdate(ScheduleOpenAPI scheduleOpenAPI){
        if(scheduleOpenAPI.getSubjectId() == null){
            throw new BadRequestException("Subject or Subject id is null!");
        }
        if(!subjectRepository.existsById(scheduleOpenAPI.getSubjectId())){
            throw new NotFoundException("Subject not found");
        }
        if(scheduleOpenAPI.getStudyGroupId() == null){
            throw new BadRequestException("Group or Group id is null!");
        }
        if(!groupRepository.existsById(scheduleOpenAPI.getStudyGroupId())){
            throw new BadRequestException("Group not found");
        }
    }
}

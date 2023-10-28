package sovcombank.jabka.studyservice.services;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sovcombank.openapi.ApiException;
import ru.sovcombank.openapi.api.UserApi;
import ru.sovcombank.openapi.model.AttendanceStatisticsOpenApi;
import ru.sovcombank.openapi.model.ERoleOpenApi;
import ru.sovcombank.openapi.model.UserOpenApi;
import sovcombank.jabka.studyservice.exceptions.BadRequestException;
import sovcombank.jabka.studyservice.exceptions.NotFoundException;
import sovcombank.jabka.studyservice.mappers.AttendanceMapper;
import sovcombank.jabka.studyservice.models.AttendanceStatistics;
import sovcombank.jabka.studyservice.models.Schedule;
import sovcombank.jabka.studyservice.models.StudyGroup;
import sovcombank.jabka.studyservice.models.Subject;
import sovcombank.jabka.studyservice.repositories.AttendanceRepository;
import sovcombank.jabka.studyservice.repositories.ScheduleRepository;
import sovcombank.jabka.studyservice.repositories.StudyGroupRepository;
import sovcombank.jabka.studyservice.repositories.SubjectRepository;
import sovcombank.jabka.studyservice.services.interfaces.AttendanceStatisticService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceStatisticServiceImpl implements AttendanceStatisticService {
    private final AttendanceRepository attendanceRepository;
    private final AttendanceMapper attendanceMapper;
    private final StudyGroupRepository groupRepository;
    private final ScheduleRepository scheduleRepository;

    private final SubjectRepository subjectRepository;
    private final UserApi userApi;


    @Override
    @Transactional
    public ResponseEntity<Void> saveStatistics(List<AttendanceStatisticsOpenApi> attendanceStatisticsOpenApi) {
        if (attendanceStatisticsOpenApi == null || attendanceStatisticsOpenApi.isEmpty()) {
            throw new BadRequestException("Attendance Statistics List is Empty");
        }
        checkAllScheduleExists(attendanceStatisticsOpenApi);
        List<AttendanceStatistics> attendanceStatisticsList = attendanceMapper.toListAttendanceStatistics(attendanceStatisticsOpenApi);
        checkAllStudentsExists(attendanceStatisticsList);
        attendanceRepository.saveAll(attendanceStatisticsList);
        return ResponseEntity
                .ok()
                .build();
    }

    @Override
    @Transactional
    public List<AttendanceStatistics> getStatisticsBySchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException(String.format("Schedule with id %d not found", scheduleId)));
        return schedule.getAttendanceStatistics().stream().toList();
    }

    @Override
    @Transactional
    public List<AttendanceStatistics> getStatisticsByGroupId(Long groupId) {
        StudyGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException(String.format("Group with id %d not found", groupId)));
        try {
            List<UserOpenApi> users = userApi.getUsersByGroupNumber(group.getName());
            return attendanceRepository.findByStudentIdIn(
                    users.stream()
                            .map(UserOpenApi::getId)
                            .toList()
            );
        } catch (ApiException e) {
            log.debug("An error occurred while fetching user information", e);
            throw new BadRequestException("An error occurred while fetching user information");
        }
    }

    @Override
    public AttendanceStatistics getById(Long id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(String.format("Statistics with id %d not found", id)));
    }

    @Override
    public List<AttendanceStatistics> getStatisticsBySubjectId(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new BadRequestException(String.format("Subject with id %d not found", subjectId)));
        return subject.getSchedule().stream()
                .flatMap(schedule -> schedule.getAttendanceStatistics().stream())
                .toList();

    }

    private void checkAllStudentsExists(List<AttendanceStatistics> attendanceStatisticsList) {
        List<Long> userIds = attendanceStatisticsList.stream()
                .map(AttendanceStatistics::getStudentId).toList();
        List<UserOpenApi> userList;
        if (userIds.size() != attendanceStatisticsList.size()) {
            throw new NotFoundException("Not all users found");
        }
        try {
            userList = userApi.getUsersByIds(userIds);
            for (UserOpenApi user : userList) {
                boolean userExistsByStudentId = user.getRoles() != null &&
                        user.getRoles()
                                .stream()
                                .anyMatch(role -> ERoleOpenApi.STUDENT.equals(role.getName()));
                if (!userExistsByStudentId) {
                    throw new NotFoundException(
                            String.format("User with id %d wasn't found", user.getId()));
                }
            }
        } catch (ApiException e) {
            log.debug("An error occurred while fetching user information", e);
            throw new BadRequestException("An error occurred while fetching user information");
        }
    }

    private void checkAllScheduleExists(List<AttendanceStatisticsOpenApi> attendanceStatisticsOpenApiList) {
        List<Schedule> schedules =
                scheduleRepository.findAllById(attendanceStatisticsOpenApiList.stream().map(AttendanceStatisticsOpenApi::getScheduleId).toList());
        Set<Long> ids = attendanceStatisticsOpenApiList.stream().map(AttendanceStatisticsOpenApi::getScheduleId).collect(Collectors.toSet());
        if(schedules.size() != ids.size()){
            throw new NotFoundException("One or more schedules weren't found");
        }
    }

}

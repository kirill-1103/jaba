package sovcombank.jabka.studyservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.sovcombank.openapi.ApiException;
import ru.sovcombank.openapi.ApiResponse;
import ru.sovcombank.openapi.api.AttendanceStatisticsApiDelegate;
import ru.sovcombank.openapi.api.UserApi;
import ru.sovcombank.openapi.model.AttendanceStatisticsOpenApi;
import ru.sovcombank.openapi.model.UserOpenApi;
import sovcombank.jabka.studyservice.exceptions.BadRequestException;
import sovcombank.jabka.studyservice.exceptions.NotFoundException;
import sovcombank.jabka.studyservice.mappers.AttendanceMapper;
import sovcombank.jabka.studyservice.models.AttendanceStatistics;
import sovcombank.jabka.studyservice.services.interfaces.AttendanceStatisticService;

import java.util.*;

import static sovcombank.jabka.studyservice.utils.ResponseApiUtils.okResponse;

@RestController
@RequestMapping("/api/study/attendance-statistics")
@RequiredArgsConstructor
public class AttendanceStatisticsController implements AttendanceStatisticsApiDelegate {
    private final AttendanceStatisticService attendanceStatisticService;
    private final AttendanceMapper attendanceMapper;

    private final UserApi userApi;

    @Override
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('TEACHER')")
    public ResponseEntity<Void> addStatistics(@RequestBody List<AttendanceStatisticsOpenApi> attendanceStatisticsOpenApi) {
        return attendanceStatisticService.saveStatistics(attendanceStatisticsOpenApi);
    }

    @Override
    @GetMapping("/group/{group_id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('TEACHER') " +
            "or hasRole('CURATOR')")
    public ResponseEntity<List<AttendanceStatisticsOpenApi>> getStatisticsByGroupId(@PathVariable("group_id") Long groupId) {
        List<AttendanceStatistics> statistics = attendanceStatisticService.getStatisticsByGroupId(groupId);
        return ResponseEntity.ok(getAttendanceOpenApiList(statistics));
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<AttendanceStatisticsOpenApi> getStatisticsById(@PathVariable Long id) {
        AttendanceStatistics attendanceStatistics = attendanceStatisticService.getById(id);
        AttendanceStatisticsOpenApi attendanceStatisticsOpenApi = attendanceMapper.toOpenApi(attendanceStatistics);
        Long userId = attendanceStatisticsOpenApi.getStudentId();
        ApiResponse<UserOpenApi> userOpenApiResponse = null;
        try {
            userOpenApiResponse = userApi.showUserInfoWithHttpInfo(userId);
            if(!okResponse(userOpenApiResponse)){
                if(userOpenApiResponse.getStatusCode() == HttpStatus.NOT_FOUND.value()){
                    throw new NotFoundException("User in AttendanceStatistics with id"+id+" not found.");
                }
            }
        } catch (ApiException e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }

        attendanceStatisticsOpenApi.setStudentId(userOpenApiResponse.getData().getId());
        return ResponseEntity.ok(attendanceStatisticsOpenApi);
    }

    @Override
    @GetMapping("/subject/{subject_id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('TEACHER')")
    public ResponseEntity<List<AttendanceStatisticsOpenApi>> getStatisticsBySubjectId(
            @PathVariable("subject_id") Long subjectId
    ) {
        List<AttendanceStatistics> statistics = attendanceStatisticService.getStatisticsBySubjectId(subjectId);

        return ResponseEntity.ok(getAttendanceOpenApiList(statistics));
    }

    @GetMapping("/schedule/{scheduleId}")
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('TEACHER')")
    public ResponseEntity<List<AttendanceStatisticsOpenApi>> getStatisticsBySchedule(
            @PathVariable("scheduleId") Long scheduleId
    ){
        List<AttendanceStatistics> statistics =
                attendanceStatisticService.getStatisticsBySchedule(scheduleId);
        return ResponseEntity.ok(getAttendanceOpenApiList(statistics));
    }

    private List<AttendanceStatisticsOpenApi> getAttendanceOpenApiList(List<AttendanceStatistics> statistics){
        List<AttendanceStatisticsOpenApi> statisticsOpenApi = attendanceMapper.toOpenApiList(statistics);
        List<Long> userIds = statisticsOpenApi.stream().map(s -> s.getStudentId()).toList();
        try {
            ApiResponse<List<UserOpenApi>> usersResponse = userApi.getUsersByIdsWithHttpInfo(userIds);
            if(!okResponse(usersResponse)){
                throw new BadRequestException();
            }
        } catch (ApiException e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }

        try {
            List<UserOpenApi> users = userApi.getUsersByIds(statistics.stream().map(AttendanceStatistics::getStudentId).toList());
            Map<Long, UserOpenApi> idUserMap = new HashMap<>();
            users.forEach(user->idUserMap.put(user.getId(),user));
            for(int i = 0;i<userIds.size();i++){
                statisticsOpenApi.get(i).setStudentId(idUserMap.get(statisticsOpenApi.get(i).getStudentId()).getId());
            }
            return statisticsOpenApi;
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }
}

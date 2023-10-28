
package sovcombank.jabka.studyservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.sovcombank.openapi.api.ScheduleApiDelegate;
import ru.sovcombank.openapi.model.ScheduleOpenAPI;
import sovcombank.jabka.studyservice.mappers.ScheduleMapper;
import sovcombank.jabka.studyservice.services.interfaces.ScheduleService;

import java.util.List;

@RestController
@RequestMapping("/api/study/schedule")
@RequiredArgsConstructor
public class ScheduleController implements ScheduleApiDelegate {

    private final ScheduleService scheduleService;
    private final ScheduleMapper scheduleMapper;

    @PostMapping
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('MODERATOR')")
    public ResponseEntity<Void> createSchedule(@RequestBody ScheduleOpenAPI scheduleOpenAPI) {
        scheduleService.createSchedule(scheduleOpenAPI);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('MODERATOR')")
    public ResponseEntity<Void> deleteSchedule(@Valid @PathVariable(name = "id") Long id) {
        scheduleService.deleteScheduleById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<List<ScheduleOpenAPI>> getAllSchedule() {
        return ResponseEntity.ok(
                scheduleMapper.toOpenAPI(scheduleService.getAll())
        );
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('MODERATOR') or hasRole('TEACHER') " +
            "or hasRole('CURATOR') or hasRole('COMMITTE')")
    public ResponseEntity<List<ScheduleOpenAPI>> getScheduleByGroupId(@Valid @PathVariable Long id) {
        return ResponseEntity.ok(
                scheduleMapper.toOpenAPI(scheduleService.getByGroupId(id))
        );
    }

    @GetMapping("/professor/{professorId}")
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('MODERATOR') or hasRole('TEACHER') " +
            "or hasRole('CURATOR') or hasRole('COMMITTE')")
    public ResponseEntity<List<ScheduleOpenAPI>> getScheduleByProfessorId(
            @Valid @PathVariable(name = "professorId") Long professorId
    ) {
        return ResponseEntity.ok(
                scheduleMapper.toOpenAPI(scheduleService.getByProfessorId(professorId))
        );
    }

    @PutMapping
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> updateSchedule(@Valid @RequestBody ScheduleOpenAPI scheduleOpenAPI) {
        scheduleService.updateSchedule(scheduleOpenAPI);
        return ResponseEntity.ok().build();
    }
}

package sovcombank.jabka.studyservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.sovcombank.openapi.api.GroupApiDelegate;
import ru.sovcombank.openapi.model.StudyGroupOpenAPI;
import ru.sovcombank.openapi.model.UserInGroupRequestOpenApi;
import sovcombank.jabka.studyservice.services.interfaces.StudyGroupService;

import java.util.List;

@RestController
@RequestMapping("/api/study/group")
@RequiredArgsConstructor
public class StudyGroupController implements GroupApiDelegate {
    private final StudyGroupService studyGroupService;

    @PostMapping
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> createGroup(@RequestBody StudyGroupOpenAPI studyGroupOpenAPI) {
        return studyGroupService.createGroup(studyGroupOpenAPI);
    }

    @DeleteMapping("/{id}")
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> deleteGroupById(@Valid @PathVariable(name = "id") Long id) {
        return studyGroupService.deleteGroupById(id);
    }

    @GetMapping
    @Override
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('TEACHER')")
    public ResponseEntity<List<StudyGroupOpenAPI>> getAllGroups() {
        return studyGroupService.getAllGroups();
    }

    @GetMapping("/{id}")
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('TEACHER') " +
            "or hasRole('STUDENT') or hasRole('CURATOR')")
    public ResponseEntity<StudyGroupOpenAPI> getGroupById(@Valid @PathVariable(name = "id") Long id) {
        return studyGroupService.getGroupById(id);
    }

    @PostMapping("/user")
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> addUserInGroup(UserInGroupRequestOpenApi userInGroupRequestOpenApi) {
        return studyGroupService.addUserInGroup(userInGroupRequestOpenApi);
    }

    @PostMapping("/users")
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> addUsersInGroup(List<UserInGroupRequestOpenApi> userInGroupRequestOpenApi) {
        return studyGroupService.addUsersInGroup(userInGroupRequestOpenApi);
    }
    @DeleteMapping("/user")
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> deleteUserFromGroup(UserInGroupRequestOpenApi userInGroupRequestOpenApi) {
        return studyGroupService.deleteUserFromGroup(userInGroupRequestOpenApi);
    }
    @DeleteMapping("/users")
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> deleteUsersFromGroup(List<UserInGroupRequestOpenApi> userInGroupRequestOpenApi) {
        return studyGroupService.deleteUsersFromGroup(userInGroupRequestOpenApi);
    }
}

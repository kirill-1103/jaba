package sovcombank.jabka.studyservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sovcombank.openapi.ApiException;
import ru.sovcombank.openapi.ApiResponse;
import ru.sovcombank.openapi.api.UserApi;
import ru.sovcombank.openapi.model.StudyGroupOpenAPI;
import ru.sovcombank.openapi.model.UpdateUserOpenApi;
import ru.sovcombank.openapi.model.UserInGroupRequestOpenApi;
import ru.sovcombank.openapi.model.UserOpenApi;
import sovcombank.jabka.studyservice.exceptions.BadRequestException;
import sovcombank.jabka.studyservice.exceptions.NotFoundException;
import sovcombank.jabka.studyservice.mappers.StudyGroupMapper;
import sovcombank.jabka.studyservice.models.StudyGroup;
import sovcombank.jabka.studyservice.repositories.StudyGroupRepository;
import sovcombank.jabka.studyservice.services.interfaces.StudyGroupService;

import java.util.*;
import java.util.stream.Collectors;

import static sovcombank.jabka.studyservice.utils.ResponseApiUtils.okResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudyGroupServiceImpl implements StudyGroupService {
    private final StudyGroupRepository groupRepository;
    private final StudyGroupMapper groupMapper;
    private final UserApi userApi;

    @Transactional
    @Override
    public ResponseEntity<Void> createGroup(StudyGroupOpenAPI studyGroupOpenAPI) {
        if (groupRepository.existsByName(studyGroupOpenAPI.getName())) {
            throw new BadRequestException("Group with such name is already exists");
        }
        StudyGroup studyGroup = groupMapper.toStudyGroup(studyGroupOpenAPI);
        groupRepository.save(studyGroup);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @Transactional
    @Override
    public ResponseEntity<Void> deleteGroupById(Long id) {
        StudyGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Group with such id %d not found", id)));
        try {
            List<UserOpenApi> groupUsers = userApi.getUsersByGroupNumber(group.getName());
            groupUsers.forEach(user -> user.setGroup(null));
            ApiResponse<Void> response = userApi.updateUsersWithHttpInfo(groupUsers);
            if (!okResponse(response)) {
                throw new BadRequestException("Cannot update group's users");
            }
        } catch (ApiException e) {
            e.printStackTrace();
            throw new BadRequestException("Cannot get/update group's users. E:" + e.getMessage());
        }
        groupRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Transactional
    @Override
    public ResponseEntity<List<StudyGroupOpenAPI>> getAllGroups() {
        List<StudyGroup> studyGroups = groupRepository.findAll();
        List<StudyGroupOpenAPI> studyGroupsOpenApi = groupMapper.toOpenAPI(studyGroups);
        studyGroupsOpenApi.forEach(this::addStudentsIdsInGroup);
        return ResponseEntity.ok(studyGroupsOpenApi);
    }

    @Transactional
    @Override
    public ResponseEntity<StudyGroupOpenAPI> getGroupById(Long id) {
        StudyGroup studyGroup = groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Groups with such id %d not found", id)));
        StudyGroupOpenAPI studyGroupOpenAPI = groupMapper.toOpenAPI(studyGroup);
        addStudentsIdsInGroup(studyGroupOpenAPI);
        return ResponseEntity.ok(studyGroupOpenAPI);
    }

    @Override
    public ResponseEntity<Void> addUserInGroup(UserInGroupRequestOpenApi userInGroupRequestOpenApi) {
        Long userId = userInGroupRequestOpenApi.getUserId();
        Long groupId = userInGroupRequestOpenApi.getGroupId();
        Optional<StudyGroup> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isEmpty()) {
            throw new NotFoundException(String.format("Group with Id %d Wasn't Found", groupId));
        }
        StudyGroup group = groupOpt.get();
        String groupName = group.getName();
        UserOpenApi user;
        try {
            user = userApi.showUserInfo(userId);
            user.setGroup(groupName);
            UpdateUserOpenApi updateUser = new UpdateUserOpenApi();
            updateUser.setNewUser(user);
            updateUser.setOldUser(user);
            userApi.updateUser(updateUser);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity
                .ok()
                .build();
    }

    @Override
    public ResponseEntity<Void> addUsersInGroup(List<UserInGroupRequestOpenApi> userInGroupRequestOpenApi) {
        Map<String, List<UserOpenApi>> users = getUsersListByGroupName(userInGroupRequestOpenApi);
        try {
            List<UserOpenApi> combinedList = users
                    .values()
                    .stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            userApi.updateUsers(combinedList);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity
                .ok()
                .build();
    }

    @Override
    public ResponseEntity<Void> deleteUserFromGroup(UserInGroupRequestOpenApi userInGroupRequestOpenApi) {
        Long userId = userInGroupRequestOpenApi.getUserId();
        Long groupId = userInGroupRequestOpenApi.getGroupId();
        String groupName = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Group with Id %d Wasn't Found", groupId)))
                .getName();
        UserOpenApi user;
        try {
            user = userApi.showUserInfo(userId);
            if(user.getGroup() != null && !user.getGroup().equals(groupName)){
                throw new BadRequestException(String.format(
                        "User With Id %d Doesn't Belong to Group With Id %d", userId, groupId));
            }
            user.setGroup(null);
            UpdateUserOpenApi updateUser = new UpdateUserOpenApi();
            updateUser.setOldUser(user);
            updateUser.setNewUser(user);
            userApi.updateUser(updateUser);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity
                .ok()
                .build();
    }

    @Override
    public ResponseEntity<Void> deleteUsersFromGroup(List<UserInGroupRequestOpenApi> userInGroupRequestOpenApi) {
        Map<Long, List<UserOpenApi>> users = getUsersListByGroupId(userInGroupRequestOpenApi);
        users
                .forEach((groupId, userFromGroup) -> userFromGroup
                                .forEach(user -> user.setGroup(null)));
        try {
            List<UserOpenApi> combinedList = users
                    .values()
                    .stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            userApi.updateUsers(combinedList);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity
                .ok()
                .build();
    }

    private void addStudentsIdsInGroup(StudyGroupOpenAPI studyGroupOpenAPI) {
        try {
            List<UserOpenApi> usersInGroup = userApi.getUsersByGroupNumber(studyGroupOpenAPI.getName());
            studyGroupOpenAPI.setStudentsIds(usersInGroup.stream()
                    .map(UserOpenApi::getId)
                    .toList()
            );
        } catch (ApiException e) {
            e.printStackTrace();
            throw new BadRequestException("Cannot load group's users");
        }
    }

    private Map<String, List<UserOpenApi>> getUsersListByGroupName(List<UserInGroupRequestOpenApi> userInGroupRequestOpenApi){
        Map<Long, List<Long>> usersGroups = new HashMap<>();
//                userInGroupRequestOpenApi.forEach(u -> usersGroups
//                .computeIfAbsent(u.getGroupId(), k -> new ArrayList<>())
//                .add(u.getUserId()));
        userInGroupRequestOpenApi
                .forEach(u -> {
                    Long groupId = u.getGroupId();
                    Long userId = u.getUserId();
                    if (!usersGroups.containsKey(groupId)) {
                        usersGroups.put(groupId, new ArrayList<>());
                    }
                    List<Long> groupUsers = usersGroups.get(groupId);
                    groupUsers.add(userId);
                });
        usersGroups
                .keySet()
                .forEach(key -> groupRepository
                        .findById(key)
                        .orElseThrow(() -> new NotFoundException(String.format(
                                "Group with Id %d Wasn't Found", key))));

        Map<String, List<Long>> userGroupNames = new HashMap<>();
        usersGroups
                .forEach((key, value) -> {
                    StudyGroup studyGroup = groupRepository.findById(key)
                            .orElseThrow(() -> new NotFoundException(String.format(
                                    "Group with Id %d Wasn't Found", key)));
                    String groupName = studyGroup.getName();
                    userGroupNames.put(groupName, value);
                });

        Map<String, List<UserOpenApi>> users = new HashMap<>();
        userGroupNames
                .forEach((groupName, value) -> {
                    List<UserOpenApi> usersToAdd;
                    try {
                        usersToAdd = userApi.getUsersByIds(value);
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                    usersToAdd.forEach(u -> u.setGroup(groupName));
                    users.put(groupName, usersToAdd);
                });
        return users;
    }

    private Map<Long, List<UserOpenApi>> getUsersListByGroupId(List<UserInGroupRequestOpenApi> userInGroupRequestOpenApi){
        Map<Long, List<Long>> usersGroups = new HashMap<>();
//        userInGroupRequestOpenApi.forEach(u -> usersGroups
//                .computeIfAbsent(u.getGroupId(), k -> new ArrayList<>())
//                .add(u.getUserId()));

        userInGroupRequestOpenApi
                .forEach(u -> {
                    Long groupId = u.getGroupId();
                    Long userId = u.getUserId();
                    if (!usersGroups.containsKey(groupId)) {
                        usersGroups.put(groupId, new ArrayList<>());
                    }
                    List<Long> groupUsers = usersGroups.get(groupId);
                    groupUsers.add(userId);
                });
        usersGroups
                .keySet()
                .forEach(key -> groupRepository
                        .findById(key)
                        .orElseThrow(() -> new NotFoundException(String.format(
                                "Group with Id %d Wasn't Found", key))));

        Map<Long, List<UserOpenApi>> users = new HashMap<>();
        usersGroups
                .forEach((id, value) -> {
                    List<UserOpenApi> usersToAdd;
                    try {
                        usersToAdd = userApi.getUsersByIds(value);
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                    users.put(id, usersToAdd);
                });
        return users;
    }
}

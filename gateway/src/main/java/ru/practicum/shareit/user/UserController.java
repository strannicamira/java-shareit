package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoToUpdate;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "${userApiPrefix}")
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        return userClient.createUser(userDto);
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") Integer id, @Valid @RequestBody UserDtoToUpdate userDto) {
        return userClient.updateUser(id, userDto);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getAllUsersById(@PathVariable("id") Integer id) {
        return userClient.getAllUsersById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();
    }

    @DeleteMapping(value = "/{userId}")
    public void deleteUserById(@PathVariable(name = "userId") Integer id) {
        userClient.deleteUserById(id);
    }
}

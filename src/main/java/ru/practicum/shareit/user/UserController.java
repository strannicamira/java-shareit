package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> findAll() {
        return userService.getAllUsers();
    }

    @GetMapping(value = "/{id}")
    public UserDto findById(@PathVariable Integer id) {
        return userService.getUser(id);
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        return userService.saveUser(userDto);
    }

    @PatchMapping(value = "/{id}")
    public UserDto update(@PathVariable("id") Integer userId, @Valid @RequestBody UserDto userDto) {
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping(value = "/{userId}")
    public void deleteUserById(@PathVariable(name = "userId") Integer userId) {
        userService.deleteUser(userId);
    }
}

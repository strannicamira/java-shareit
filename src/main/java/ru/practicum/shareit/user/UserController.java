package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping(value = "/{id}")
    public User findById(@PathVariable Integer id) {
        return userService.findById(id);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PatchMapping(value = "/{id}")
    public UserDto update(@PathVariable("id") Integer id, @Valid @RequestBody UserDto userDto) {
        return userMapper.toUserDto(userService.update(id, userMapper.toUser(userDto)));
    }

    @DeleteMapping(value = "/{userId}")
    public void deleteUserById(@PathVariable(name = "userId") Integer id) {
        userService.deleteUserById(id);
    }
}

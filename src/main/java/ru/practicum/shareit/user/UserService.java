package ru.practicum.shareit.user;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUser(Integer userId);

    UserDto saveUser(UserDto userDto);

    UserDto updateUser(Integer userId, UserDto userDto);

    void deleteUser(Integer userId);
}

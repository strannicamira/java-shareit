package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User findById(Integer id);

    User create(User user);

    UserDto update(Integer id, UserDto userDto);

    void deleteUserById(Integer id);
}

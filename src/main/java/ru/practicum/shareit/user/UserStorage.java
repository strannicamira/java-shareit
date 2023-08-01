package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {
    List<User> getAllUsers();

    User getUser(Integer id);

    User saveUser(User user);

    User updateUser(Integer id, User user);

    void deleteUser(Integer id);
}

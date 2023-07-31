package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User findById(Integer id);

    User create(User user);

    User update(Integer id, User user);

    void deleteUserById(Integer id);
}

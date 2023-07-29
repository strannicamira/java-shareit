package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public List<User> findAll() {
        return userStorage.findAll();
    }

    @Override
    public User findById(Integer id) {
        return userStorage.findById(id);
    }

    @Override
    public User create(User user) {
        return userStorage.create(user);
    }

    @Override
    public UserDto update(Integer id, UserDto userDto) {
        return userStorage.update(id, userDto);
    }

    @Override
    public void deleteUserById(Integer id) {
        userStorage.deleteUserById(id);
    }
}

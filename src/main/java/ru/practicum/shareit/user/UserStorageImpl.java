package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateEmailFoundException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Primary
@RequiredArgsConstructor
@Slf4j
public class UserStorageImpl implements UserStorage {

    private Map<Integer, User> users = new ConcurrentHashMap<>();
    private Integer id = 0;

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(Integer id) {
        return Optional.ofNullable(users.get(id)).orElseThrow(() ->
                new NotFoundException("Пользователь не найден в списке."));
    }


    public User findByEmail(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User saveUser(User user) {
        if (findByEmail(user.getEmail()) != null) {
            throw new DuplicateEmailFoundException("Пользователь с такой почтой уже существует");
        }
        user.setId(++id);
        users.put(id, user);
        return user;
    }

    @Override
    public User updateUser(Integer id, User user) {
        User userWithTheSameEmail = findByEmail(user.getEmail());
        if (userWithTheSameEmail != null && !userWithTheSameEmail.getId().equals(id)) {
            throw new DuplicateEmailFoundException("Пользователь с такой почтой уже существует");
        }
        User obsoledUser = getUser(id);
        user.setId(id);
        user.setName(user.getName() == null ? obsoledUser.getName() : user.getName());
        user.setEmail(user.getEmail() == null ? obsoledUser.getEmail() : user.getEmail());
        users.put(id, user);
        return user;
    }

    @Override
    public void deleteUser(Integer id) {
        users.remove(id);
    }
}

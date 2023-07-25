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
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(Integer id) {
        return Optional.ofNullable(users.get(id)).orElseThrow(() -> new NotFoundException("Пользователь не найден в списке."));
    }


    public User findByEmail(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) ;
            return user;
        }
        return null;
    }

    @Override
    public User create(User user) {
        if (findByEmail(user.getEmail()) != null) {
            throw new DuplicateEmailFoundException("Пользователь с такой почтой уже существует");
        }
        user.setId(++id);
        users.put(id, user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUserById(Integer id) {
        users.remove(id);
    }
}

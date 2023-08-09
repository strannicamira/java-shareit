package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        log.info("Search all users");
        return UserMapper.mapToUserDto(repository.findAll());
    }

    @Override
    @Transactional
    public UserDto getUser(Integer userId) {
        log.info("Search user by id {}", userId);
        User user = repository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto saveUser(UserDto userDto) {
        log.info("Create user");
        User user = repository.save(UserMapper.mapToUser(userDto));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(Integer userId, UserDto userDto) {
        log.info("Update user by id {}", userId);

        //TODO: extract method
        User userById = repository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        //TODO: extract method
        userById.setName(userDto.getName() == null || userDto.getName().isBlank() ? userById.getName() : userDto.getName());
        userById.setEmail(userDto.getEmail() == null || userDto.getEmail().isBlank() ? userById.getEmail() : userDto.getEmail());

        User savedUser = repository.saveAndFlush(userById);

        return UserMapper.mapToUserDto(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        log.info("Delete user by id {}", userId);
        repository.deleteById(userId);
    }
}

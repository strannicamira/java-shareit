package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;


import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=shareittest",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplIntegrationTest {

    private final UserService userService;
    private final UserRepository repository;

    @Test
    void createUser() {
        UserDto userDto = makeUserDto("John Doe", "some@email.com");

        UserDto createdUserDto = userService.createUser(userDto);
        Integer createdUserId = createdUserDto.getId();

        User user = repository.findById(createdUserId).orElseThrow(() -> new NotFoundException("User not found from saveUser"));

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void updateUser() {
        UserDto userDto = makeUserDto("John Doe", "some@email.com");
        UserDto savedUserDto = userService.createUser(userDto);

        Integer userId = savedUserDto.getId();
        UserDto userDtoToUpdate = makeUserDto("Up Date", "update@email.com");

        userService.updateUser(userId, userDtoToUpdate);

        User foundUser = repository.findById(userId).orElseThrow(() -> new NotFoundException("User not found from updateUser"));

        assertThat(foundUser.getId(), equalTo(userId));
        assertThat(foundUser.getName(), equalTo(userDtoToUpdate.getName()));
        assertThat(foundUser.getEmail(), equalTo(userDtoToUpdate.getEmail()));
    }


    @Test
    void updateUserName() {
        UserDto userDto = makeUserDto("John Doe", "some@email.com");
        UserDto savedUserDto = userService.createUser(userDto);

        Integer userId = savedUserDto.getId();
        UserDto userDtoToUpdate = makeUserDtoByName("Up Date");

        userService.updateUser(userId, userDtoToUpdate);

        User savedUser = repository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        assertThat(savedUser.getId(), equalTo(userId));
        assertThat(savedUser.getName(), equalTo(userDtoToUpdate.getName()));
        assertThat(savedUser.getEmail(), equalTo(userDto.getEmail()));
    }

    //TODO: updateUserEmail


    @Test
    void updateUser_whenIdDoesntExist_thenThrowNotFoundException() {
        UserDto userDto = makeUserDto("John Doe", "some@email.com");
        UserDto savedUserDto = userService.createUser(userDto);

        Integer userId = 100;
        UserDto userDtoToUpdate = makeUserDto("Up Date", "update@email.com");
//        User savedMockedUser = new User(1, "Up Date", "update@email.com");

        UserDto userDtoToSave = makeUserDto("John Doe", "some@email.com");

        assertThrows(NotFoundException.class, () -> userService.updateUser(userId, userDtoToUpdate));
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

    private UserDto makeUserDtoByName(String name) {
        UserDto dto = new UserDto();
        dto.setName(name);
        return dto;
    }

}

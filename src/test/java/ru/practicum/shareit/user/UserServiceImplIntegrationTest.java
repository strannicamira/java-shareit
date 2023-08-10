package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@Transactional
@SpringBootTest(
        properties = "db.name=shareittest",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceImplIntegrationTest {

    private final UserService userService;
    private final UserRepository repository;

    private static int index = 0;

    @Order(1)
    @Test
    void createUser() {
        log.info("UserServiceImplIntegrationTest-" + index++ + "-createUser");

        UserDto userDto = makeUserDto("John Doe", "some@email.com");

        UserDto createdUserDto = userService.createUser(userDto);
        Integer createdUserId = createdUserDto.getId();

        User user = repository.findById(createdUserId).orElseThrow(() -> new NotFoundException("User not found from saveUser"));

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Order(2)
    @Test
    void updateUser() {
        log.info("UserServiceImplIntegrationTest-" + index++ + "-updateUser");

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


    @Order(3)
    @Test
    void updateUserName() {
        log.info("UserServiceImplIntegrationTest-" + index++ + "-updateUserName");

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

    @Order(4)
    @Test
    void updateUserEmail() {
        log.info("UserServiceImplIntegrationTest-" + index++ + "-updateUserName");

        UserDto userDto = makeUserDto("John Doe", "some@email.com");
        UserDto savedUserDto = userService.createUser(userDto);
        Integer userId = savedUserDto.getId();

        UserDto userDtoToUpdate = makeUserDtoByEmail("update@email.com");

        userService.updateUser(userId, userDtoToUpdate);

        User savedUser = repository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        assertThat(savedUser.getId(), equalTo(userId));
        assertThat(savedUser.getName(), equalTo(userDto.getName()));
        assertThat(savedUser.getEmail(), equalTo(userDtoToUpdate.getEmail()));
    }

    @Order(5)
    @Test
    void updateUser_whenIdDoesntExist_thenThrowNotFoundException() {
        log.info("UserServiceImplIntegrationTest-" + index++ + "-updateUser_");

        UserDto userDto = makeUserDto("John Doe", "some@email.com");
        UserDto savedUserDto = userService.createUser(userDto);

        Integer userId = 100;
        UserDto userDtoToUpdate = makeUserDto("Up Date", "update@email.com");
//        User savedMockedUser = new User(1, "Up Date", "update@email.com");

        UserDto userDtoToSave = makeUserDto("John Doe", "some@email.com");

        assertThrows(NotFoundException.class, () -> userService.updateUser(userId, userDtoToUpdate));
    }

    @Order(6)
    @Test
    void getUser() {
        log.info("UserServiceImplIntegrationTest-" + index++ + "-getUser");

        UserDto userDto = makeUserDto("John Doe", "some@email.com");

        UserDto createdUserDto = userService.createUser(userDto);
        Integer createdUserId = createdUserDto.getId();

        UserDto gotdUserDto = userService.getUser(createdUserId);

        assertThat(createdUserId, equalTo(gotdUserDto.getId()));
        assertThat(userDto.getName(), equalTo(gotdUserDto.getName()));
        assertThat(userDto.getEmail(), equalTo(gotdUserDto.getEmail()));

    }

    @Order(7)
    @Test
    void getUser_whenIdDoesntExist_thenThrowNotFoundException() {
        log.info("UserServiceImplIntegrationTest-" + index++ + "-getUser_");

        int userId = 999;

        assertThrows(NotFoundException.class, () -> userService.getUser(userId));


    }


    @Order(8)
    @Test
    void getAllUsers() {
        log.info("UserServiceImplIntegrationTest-" + index++ + "-getUser");
        UserDto userDto = makeUserDto("John Doe", "some@email.com");
        UserDto createdUserDto = userService.createUser(userDto);
        Integer createdUserId = createdUserDto.getId();
        List<UserDto> userDtos = Arrays.asList(createdUserDto);

        List<UserDto> gotdUserDtos = userService.getAllUsers();

        assertThat(gotdUserDtos.size(), equalTo(userDtos.size()));
        for (int i = 0; i < gotdUserDtos.size(); i++) {
            assertThat(gotdUserDtos.get(i).getId(), equalTo(userDtos.get(i).getId()));
            assertThat(gotdUserDtos.get(i).getName(), equalTo(userDtos.get(i).getName()));
            assertThat(gotdUserDtos.get(i).getEmail(), equalTo(userDtos.get(i).getEmail()));
        }


    }


    @Order(9)
    @Test
    void deleteUser() {
        log.info("UserServiceImplIntegrationTest-" + index++ + "-deleteUser");

        UserDto userDto = makeUserDto("John Doe", "some@email.com");

        UserDto createdUserDto = userService.createUser(userDto);
        Integer createdUserId = createdUserDto.getId();

        User user = repository.findById(createdUserId).orElseThrow(() -> new NotFoundException("User not found from saveUser"));

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));

        userService.deleteUser(createdUserId);
        assertThat(repository.findById(createdUserId),equalTo(Optional.empty()));
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

    private UserDto makeUserDtoByEmail(String email) {
        UserDto dto = new UserDto();
        dto.setEmail(email);
        return dto;
    }

}

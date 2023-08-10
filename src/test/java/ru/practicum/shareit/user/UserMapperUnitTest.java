package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name=shareittest",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserMapperUnitTest {
    //TODO: ?
    @Test
    void mapToUserTest() {
        UserDto userDto = new UserDto(1, "John Doe", "some@email.com");
        User expectedUser = new User(1, "John Doe", "some@email.com");
        User mappedUser = UserMapper.mapToUser(userDto);
        assertThat(mappedUser.getId(), equalTo(expectedUser.getId()));
        assertThat(mappedUser.getName(), equalTo(expectedUser.getName()));
        assertThat(mappedUser.getEmail(), equalTo(expectedUser.getEmail()));
    }

    @Test
    void mapToUserDtoTest() {
        UserDto expectedUserDto = new UserDto(1, "John Doe", "some@email.com");
        User user = new User(1, "John Doe", "some@email.com");
        UserDto mappedUserDto = UserMapper.mapToUserDto(user);
        assertThat(mappedUserDto.getId(), equalTo(expectedUserDto.getId()));
        assertThat(mappedUserDto.getName(), equalTo(expectedUserDto.getName()));
        assertThat(mappedUserDto.getEmail(), equalTo(expectedUserDto.getEmail()));
    }

    @Test
    void mapToListOfUserDtoTest() {
        UserDto expectedUserDto1 = new UserDto(1, "John Doe", "john@email.com");
        UserDto expectedUserDto2 = new UserDto(2, "Jane Doe", "jane@email.com");

        User user1 = new User(1, "John Doe", "john@email.com");
        User user2 = new User(2, "Jane Doe", "jane@email.com");

        List<User> users = Arrays.asList(user1, user2);
        List<UserDto> expectedUserDtos = Arrays.asList(expectedUserDto1, expectedUserDto2);


        List<UserDto> mappedUserDtos = UserMapper.mapToUserDto(users);

        for (int i = 0; i < mappedUserDtos.size(); i++) {
            assertThat(mappedUserDtos.get(i).getId(), equalTo(expectedUserDtos.get(i).getId()));
            assertThat(mappedUserDtos.get(i).getName(), equalTo(expectedUserDtos.get(i).getName()));
            assertThat(mappedUserDtos.get(i).getEmail(), equalTo(expectedUserDtos.get(i).getEmail()));
        }
    }


    private UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }
}

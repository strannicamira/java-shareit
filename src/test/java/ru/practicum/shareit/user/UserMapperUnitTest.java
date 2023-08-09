package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
    void mapToUserTest(){
        UserDto userDto = new UserDto(1, "John Doe", "some@email.com");
        User expectedUser = new User(1, "John Doe", "some@email.com");
        User mappedUser = UserMapper.mapToUser(userDto);
        assertThat(mappedUser.getId(), equalTo(expectedUser.getId()));
        assertThat(mappedUser.getName(), equalTo(expectedUser.getName()));
        assertThat(mappedUser.getEmail(), equalTo(expectedUser.getEmail()));

//        assertThat(mappedUser, equalTo(expectedUser));
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }
}

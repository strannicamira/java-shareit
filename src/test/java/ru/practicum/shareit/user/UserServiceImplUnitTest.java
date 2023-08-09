package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

@Transactional
@SpringBootTest(
        properties = "db.name=shareittest",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplUnitTest {
    //TODO: ?
    @Mock
    private final UserRepository mockUserRepository;

    @Test
    void saveUser() {
        UserService userService = new UserServiceImpl(mockUserRepository);
        // given
        UserDto userDto = makeUserDto("John Doe", "some@email.com");
        User mockUser = new User(1, "John Doe", "some@email.com");
        UserDto expectedUserDto = new UserDto(1, "John Doe", "some@email.com");

        // when
        Mockito
                .when(mockUserRepository.save(Mockito.any()))
                .thenReturn(mockUser);


        // then
        UserDto savedUserDto = userService.saveUser(userDto);


        assertThat(savedUserDto.getId(), equalTo(expectedUserDto.getId()));
        assertThat(savedUserDto.getName(), equalTo(expectedUserDto.getName()));
        assertThat(savedUserDto.getEmail(), equalTo(expectedUserDto.getEmail()));

//        assertThat(savedUserDto, equalTo(expectedUserDto));
    }

    @Test
    void updateUser_whenIdDoesntExist_thenThrowNotFoundException() {
        UserService userService = new UserServiceImpl(mockUserRepository);

        Integer userId = 2;
        UserDto userDtoToUpdate = makeUserDto("Up Date", "update@email.com");
//        User savedMockedUser = new User(1, "Up Date", "update@email.com");
        Mockito
                .when(mockUserRepository.findById(anyInt())).thenThrow(NotFoundException.class);


        UserDto userDtoToSave = makeUserDto("John Doe", "some@email.com");
        //userService.updateUser(userId, userDtoToSave);

        assertThrows(NotFoundException.class, () -> userService.updateUser(userId, userDtoToUpdate));

    }


    private UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

}

package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.*;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;

@Transactional
@SpringBootTest(
        properties = "db.name=shareittest",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplUnitTest {
    //TODO: ?

    @Mock
    private final ItemRepository mockItemRepository;
    @Mock
    private final UserRepository mockUserRepository;
    @Mock
    private final BookingService mockBookingService;
    @Mock
    private final CommentRepository mockCommentRepository;
    @Mock
    private final ItemRequestRepository mockRequestRepository;
//    @Mock
//    private final UserMapper userMapper;

    @Test
    void updateUser_whenIdDoesntExist_thenThrowNotFoundException() {
        UserService userService = new UserServiceImpl(mockUserRepository);
        UserDto userDtoToCreate = makeUserDto("User Name", "user@email.com");
//        UserDto user = userService.createUser(userDtoToCreate);
        User user = new User(1,"User Name", "user@email.com");
        Integer userId = user.getId();

        Mockito
                .when(mockUserRepository.findById(anyInt())).thenReturn(Optional.of(user));


        ItemDto itemDto = makeItemDto("Something", "Some thing");
        ItemService itemService = new ItemServiceImpl(mockItemRepository, mockUserRepository, mockBookingService, mockCommentRepository, mockRequestRepository);
//        ItemDto createdItemDto = itemService.createItem(userId, itemDto);
//        Integer itemId = createdItemDto.getId();

        ItemDtoForUpdate itemDtoForUpdate = makeItemDtoForUpdateByName("Updatedthing");

        Mockito
                .when(mockItemRepository.findById(anyInt())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.updateItem(userId, itemDtoForUpdate,  anyInt()));

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

    private ItemDto makeItemDto(String name, String description) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(Boolean.TRUE);
        return dto;
    }

    private ItemDtoForUpdate makeItemDtoForUpdate(String name, String description, Boolean available) {
        ItemDtoForUpdate dto = new ItemDtoForUpdate();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        return dto;
    }

    private ItemDtoForUpdate makeItemDtoForUpdateByName(String name) {
        ItemDtoForUpdate dto = new ItemDtoForUpdate();
        dto.setName(name);
        return dto;
    }

}

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
import ru.practicum.shareit.user.*;

import java.util.Optional;

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

    @Test
    void updateItem_whenIdDoesntExist_thenThrowNotFoundException() {
        ItemService itemService = new ItemServiceImpl(mockItemRepository, mockUserRepository, mockBookingService, mockCommentRepository, mockRequestRepository);

        User user = new User(1,"User Name", "user@email.com");
        Integer userId = user.getId();

        ItemDtoForUpdate itemDtoForUpdate = makeItemDtoForUpdateByName("Updatedthing");

        Mockito
                .when(mockUserRepository.findById(anyInt())).thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findById(anyInt())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.updateItem(userId, itemDtoForUpdate,  anyInt()));
    }

    private ItemDtoForUpdate makeItemDtoForUpdateByName(String name) {
        ItemDtoForUpdate dto = new ItemDtoForUpdate();
        dto.setName(name);
        return dto;
    }

}

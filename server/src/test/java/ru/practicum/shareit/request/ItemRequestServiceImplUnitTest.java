package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
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
public class ItemRequestServiceImplUnitTest {
    //TODO: ?

    @Mock
    private final ItemRepository mockItemRepository;
    @Mock
    private final ItemRequestRepository mockItemRequestRepository;
    @Mock
    private final UserRepository mockUserRepository;


    @Test
    void getAllListOfItemRequestsByUserId_whenUserDoesnotExist_thenThrowNotFoundException() {
        ItemRequestService itemRequestService = new ItemRequestServiceImpl(mockItemRequestRepository, mockUserRepository, mockItemRepository);
        User user = new User(1, "User Name", "user@email.com");
        Integer from = -10;
        Integer size = 10;
        Mockito
                .when(mockUserRepository.findById(anyInt())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.get(user.getId(), from, size));
    }

    @Test
    void getAllListOfItemRequestsByUserId_whenFromIsNegative_thenThrowIllegalStateException() {
        ItemRequestService itemRequestService = new ItemRequestServiceImpl(mockItemRequestRepository, mockUserRepository, mockItemRepository);
        User user = new User(1, "User Name", "user@email.com");
        Integer from = -10;
        Integer size = 10;
        Mockito
                .when(mockUserRepository.findById(anyInt())).thenReturn(Optional.of(user));

        assertThrows(IllegalStateException.class, () -> itemRequestService.get(user.getId(), from, size));
    }

    @Test
    void getAllListOfItemRequestsByUserId_whenSizeIsNegative_thenThrowIllegalStateException() {
        ItemRequestService itemRequestService = new ItemRequestServiceImpl(mockItemRequestRepository, mockUserRepository, mockItemRepository);
        User user = new User(1, "User Name", "user@email.com");
        Integer from = 10;
        Integer size = -10;
        Mockito
                .when(mockUserRepository.findById(anyInt())).thenReturn(Optional.of(user));

        assertThrows(IllegalStateException.class, () -> itemRequestService.get(user.getId(), from, size));
    }

    @Test
    void getAllListOfItemRequestsByUserId_whenFromIsNull_thenReturnEmptyList() {
        ItemRequestService itemRequestService = new ItemRequestServiceImpl(mockItemRequestRepository, mockUserRepository, mockItemRepository);
        User user = new User(1, "User Name", "user@email.com");
        Integer from = null;
        Integer size = -10;
        Mockito
                .when(mockUserRepository.findById(anyInt())).thenReturn(Optional.of(user));

        List<ItemRequestDto> itemRequestDtos = itemRequestService.get(user.getId(), from, size);

        assertThat(new ArrayList<>(), equalTo(itemRequestDtos));
    }


    @Test
    void getAllListOfItemRequestsByUserId_whenSizeIsNull_thenReturnEmptyList() {
        ItemRequestService itemRequestService = new ItemRequestServiceImpl(mockItemRequestRepository, mockUserRepository, mockItemRepository);
        User user = new User(1, "User Name", "user@email.com");
        Integer from = -10;
        Integer size = null;
        Mockito
                .when(mockUserRepository.findById(anyInt())).thenReturn(Optional.of(user));

        List<ItemRequestDto> itemRequestDtos = itemRequestService.get(user.getId(), from, size);

        assertThat(new ArrayList<>(), equalTo(itemRequestDtos));
    }

}

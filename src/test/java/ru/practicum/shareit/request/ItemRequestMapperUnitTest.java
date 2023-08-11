package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
@SpringBootTest(
        properties = "db.name=shareittest",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestMapperUnitTest {
    //TODO: ?
    @Test
    void mapToItemRequestTest() {
//        User user = new User(1, "John Doe", "some@email.com");
//        ItemDto itemDto = makeItemDto("Something", "Some thing", Boolean.TRUE, null);
//        List<ItemDto> itemDtos = Arrays.asList(itemDto);
        User user2 = new User(2, "Jane Doe", "jane@email.com");
        ItemRequestDto itemRequestDto = makeItemDtoRequest(1, "My first request", LocalDateTime.now());

        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(itemRequestDto, user2);

        assertAll(
                () -> assertThat(itemRequest.getId(), equalTo(itemRequestDto.getId())),
                () -> assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription())),
                () -> assertThat(itemRequest.getCreated(), equalTo(itemRequestDto.getCreated()))
        );
    }

    @Test
    void mapToItemRequestDtoTest() {
//        User user1 = new User(1, "John Doe", "john@email.com");
        ItemDto itemDto = makeItemDto("Something", "Some thing", Boolean.TRUE, 1);
        List<ItemDto> itemDtos = Arrays.asList(itemDto);

        User user2 = new User(2, "Jane Doe", "jane@email.com");
        ItemRequest itemRequest = new ItemRequest(1, "My first request", user2, LocalDateTime.now());
//        Item item = new Item(1, "Something", "Some thing", Boolean.TRUE, user1, itemRequest);

        ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(itemRequest, itemDtos);

        assertAll(
                () -> assertThat(itemRequest.getId(), equalTo(itemRequestDto.getId())),
                () -> assertThat(itemRequest.getCreated(), equalTo(itemRequestDto.getCreated())),
                () -> assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()))
        );

        assertThat(itemRequestDto.getItems().size(), equalTo(itemDtos.size()));

        for (int i = 0; i < itemDtos.size(); i++) {
            int finalI = i;
            assertAll(
                    () -> assertThat(itemDtos.get(finalI).getId(), equalTo(itemRequestDto.getItems().get(finalI).getId())),
                    () -> assertThat(itemDtos.get(finalI).getName(), equalTo(itemRequestDto.getItems().get(finalI).getName())),
                    () -> assertThat(itemDtos.get(finalI).getDescription(), equalTo(itemRequestDto.getItems().get(finalI).getDescription())),
                    () -> assertThat(itemDtos.get(finalI).getAvailable(), equalTo(itemRequestDto.getItems().get(finalI).getAvailable())),
//                    () -> assertThat(users.get(finalI).getId(), equalTo(itemRequestDto.getItems().get(finalI).getOwner().getId())),
                    () -> assertThat(itemDtos.get(finalI).getRequestId(), equalTo(itemRequestDto.getItems().get(finalI).getRequestId()))
            );
        }
    }

    private ItemDto makeItemDto(String name, String description, Boolean available, Integer request) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        dto.setRequestId(request);
        return dto;
    }

    private static ItemRequestDto makeItemDtoRequest(Integer id, String description, LocalDateTime created) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(id);
        dto.setDescription(description);
        dto.setCreated(created);
        return dto;
    }

}

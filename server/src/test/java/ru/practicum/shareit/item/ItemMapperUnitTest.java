package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.ItemRequest;
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
public class ItemMapperUnitTest {
    //TODO: ?
    @Test
    void mapToItemTest() {
        User user = new User(1, "John Doe", "some@email.com");
        ItemDto itemDto = makeItemDto("Something", "Some thing", Boolean.TRUE, null);

        Item item = ItemMapper.mapToItem(itemDto, user);

        assertAll(
//                () -> assertThat(item.getId(), equalTo(itemDto.getId())),
                () -> assertThat(item.getName(), equalTo(itemDto.getName())),
                () -> assertThat(item.getDescription(), equalTo(itemDto.getDescription())),
                () -> assertThat(item.getOwner().getId(), equalTo(user.getId()))
//        () -> assertThat(item.getItemRequest().getId(), equalTo(itemDto.getRequestId()))
        );

    }

    @Test
    void mapToItemDtoTest() {
        User user1 = new User(1, "John Doe", "john@email.com");
        User user2 = new User(2, "Jane Doe", "jane@email.com");

        ItemRequest itemRequest = new ItemRequest(1, "My first request", user2, LocalDateTime.now());
        Item item = new Item(1, "Something", "Some thing", Boolean.TRUE, user1, itemRequest);

        ItemDto itemDto = ItemMapper.mapToItemDto(item);

        assertAll(
                () -> assertThat(itemDto.getId(), equalTo(item.getId())),
                () -> assertThat(itemDto.getName(), equalTo(item.getName())),
                () -> assertThat(itemDto.getDescription(), equalTo(item.getDescription())),
                () -> assertThat(itemDto.getAvailable(), equalTo(item.getAvailable())),
                () -> assertThat(user1.getId(), equalTo(item.getOwner().getId())),
                () -> assertThat(itemDto.getRequestId(), equalTo(item.getItemRequest().getId()))
        );
    }

    @Test
    void mapToItemDtoTest_whenitemRequestIsNull() {
        User user1 = new User(1, "John Doe", "john@email.com");
        User user2 = new User(2, "Jane Doe", "jane@email.com");

//        ItemRequest itemRequest= new ItemRequest(1, "My first request",user2, LocalDateTime.now());
        Item item = new Item(1, "Something", "Some thing", Boolean.TRUE, user1, null);

        ItemDto itemDto = ItemMapper.mapToItemDto(item);

        assertAll(
                () -> assertThat(itemDto.getId(), equalTo(item.getId())),
                () -> assertThat(itemDto.getName(), equalTo(item.getName())),
                () -> assertThat(itemDto.getDescription(), equalTo(item.getDescription())),
                () -> assertThat(itemDto.getAvailable(), equalTo(item.getAvailable())),
                () -> assertThat(user1.getId(), equalTo(item.getOwner().getId())),
                () -> assertThat(itemDto.getRequestId(), equalTo(null))
        );
    }


    @Test
    void mapToListOfItemDtoTest() {
        User user1 = new User(1, "John Doe", "john@email.com");
        User user2 = new User(2, "Jane Doe", "jane@email.com");

        ItemRequest itemRequest = new ItemRequest(1, "My first request", user2, LocalDateTime.now());
        Item item = new Item(1, "Something", "Some thing", Boolean.TRUE, user1, itemRequest);

        List<User> users = Arrays.asList(user1);
        List<Item> items = Arrays.asList(item);


        List<ItemDto> itemDtos = ItemMapper.mapToItemDto(items);

        assertThat(itemDtos.size(), equalTo(items.size()));

        for (int i = 0; i < itemDtos.size(); i++) {
            int finalI = i;
            assertAll(
                    () -> assertThat(itemDtos.get(finalI).getId(), equalTo(items.get(finalI).getId())),
                    () -> assertThat(itemDtos.get(finalI).getName(), equalTo(items.get(finalI).getName())),
                    () -> assertThat(itemDtos.get(finalI).getDescription(), equalTo(items.get(finalI).getDescription())),
                    () -> assertThat(itemDtos.get(finalI).getAvailable(), equalTo(items.get(finalI).getAvailable())),
                    () -> assertThat(users.get(finalI).getId(), equalTo(items.get(finalI).getOwner().getId())),
                    () -> assertThat(itemDtos.get(finalI).getRequestId(), equalTo(items.get(finalI).getItemRequest().getId()))
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

}

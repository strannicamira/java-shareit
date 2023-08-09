package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
@Transactional
@SpringBootTest(
        properties = "db.name=shareittest",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplIntegrationTest {

    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Test
    void createItem() {
        UserDto userDto = makeUserDto("John Doe", "some@email.com");
        UserDto savedUserDto = userService.createUser(userDto);

        ItemDto itemDto = makeItemDto("Something", "Some thing");
        ItemDto itemDtoCreated = itemService.createItem(savedUserDto.getId(), itemDto);
        Integer itemId = itemDtoCreated.getId();

        log.info("Item created by id " + itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found by id " + itemId));

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(Boolean.TRUE));
        assertThat(item.getItemRequest(), equalTo(null));
    }

    @Test
    void createItemComment() {
        //TODO:
    }

    @Test
    void updateUser() {
        UserDto userDto = makeUserDto("John Doe", "some@email.com");
        UserDto savedUserDto = userService.createUser(userDto);
        Integer userId = savedUserDto.getId();

        ItemDto itemDto = makeItemDto("Something", "Some thing");
        ItemDto item = itemService.createItem(savedUserDto.getId(), itemDto);
        Integer itemId = item.getId();
        log.info("Item created by id " + itemId);

        ItemDtoForUpdate itemDtoForUpdate = makeItemDtoForUpdate("Updatething", "Update thing", Boolean.TRUE);
        ItemDto itemUpdated = itemService.updateItem(userId, itemDtoForUpdate, itemId);
        Integer itemIdUpdated = itemUpdated.getId();
        log.info("Item updated by id " + itemIdUpdated);

        Item updatedItem = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));

        assertThat(updatedItem.getId(), equalTo(itemId));
        assertThat(updatedItem.getName(), equalTo(itemDtoForUpdate.getName()));
        assertThat(updatedItem.getDescription(), equalTo(itemDtoForUpdate.getDescription()));
        assertThat(updatedItem.getAvailable(), equalTo(itemDtoForUpdate.getAvailable()));
        assertThat(updatedItem.getItemRequest(), equalTo(null));
        assertThat(updatedItem.getOwner().getId(), equalTo(userId));

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

}

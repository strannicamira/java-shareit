package ru.practicum.shareit.item;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@Transactional
@SpringBootTest(
        properties = "db.name=shareittest",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemServiceImplIntegrationTest {

    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserService userService;

    private static List<Integer> itemOwners;
    private static List<Integer> items;
    private static List<Integer> notOwners;

    @BeforeAll
    static void setup() {
        itemOwners = new ArrayList<>();
        items = new ArrayList<>();
        notOwners = new ArrayList<>();
    }

    @Order(10)
    @Test
    void createItem() {
        UserDto userDto = makeUserDto("John Doe", "some@email.com");
        UserDto savedUserDto = userService.createUser(userDto);

        ItemDto itemDto = makeItemDto("Something", "Some thing");
        ItemDto itemDtoCreated = itemService.createItem(savedUserDto.getId(), itemDto);
        Integer itemId = itemDtoCreated.getId();

        log.info("Item created by id " + itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found by id " + itemId));

        assertJustCreatedItems(itemDto, item);
    }


    @Order(70)
    @Test
    void createItemComment() {
        //TODO:
    }

    @Order(20)
    @Test
    void updateItem() {
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

        assertItems(userId, itemId, itemDtoForUpdate, updatedItem);

    }


    @Order(30)
    @Test
    void getItem() {
        ItemData itemData = makeItemDataStepByStep(true);
        Integer itemOwnerId = itemData.itemOwner.getId();
        Integer itemId = itemData.getItem().getId();

        ItemDto itemDto = itemService.getItem(itemOwnerId, itemId);

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));

        assertItems(item, itemDto);
    }

    @Order(31)
    @Test
    void getItem_whenUserIsNotFound_thenThrowNotFoundException() {
        ItemData itemData = makeItemDataStepByStep(true);
        Integer itemOwnerId = itemData.getItemOwner().getId();
        Integer itemId = itemData.getItem().getId();
//
//        UserDto userDto = new UserDto(itemOwnerId+1, "NotFound",  "notfound@email.com");
//        Integer userDtoId = userDto.getId();
//        notOwners.add(userDtoId);

        assertThrows(NotFoundException.class, () -> itemService.getItem(itemOwnerId + 1, itemId));
    }

    @Order(32)
    @Test
    void getItem_whenItemIsNotFound_thenThrowNotFoundException() {
        ItemData itemData = makeItemDataStepByStep(true);
        Integer itemOwnerId = itemData.getItemOwner().getId();
        Integer itemId = itemData.getItem().getId();

//        UserDto userDto = new UserDto(itemOwnerId+1, "NotFound",  "notfound@email.com");
//        Integer userDtoId = userDto.getId();
//        notOwners.add(userDtoId);

        assertThrows(NotFoundException.class, () -> itemService.getItem(itemOwnerId, itemId + 1));
    }








    @Order(40)
    @Test
    void getUserItemsWithBooking() {
        ItemData itemData = makeItemDataStepByStep(true);
        Integer itemOwnerId = itemData.itemOwner.getId();
        Integer itemId = itemData.getItem().getId();

        List<ItemWithBookingDto> itemWithBookingDtos = itemService.getUserItemsWithBooking(itemOwnerId);

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
//        itemRepository.findByIdIn(List ids)

//        assertItems(item, itemDto);
    }


    @Order(50)
    @Test
    void getUserItems() {
        ItemData itemData = makeItemDataStepByStep(true);
        Integer itemOwnerId = itemData.itemOwner.getId();
        Integer itemId = itemData.getItem().getId();

        String text = "item";
        List<ItemDto> itemDtos = itemService.getUserItems(itemOwnerId, text);

        BooleanExpression byAvailable = QItem.item.available.eq(true);
        BooleanExpression byTextInName = QItem.item.name.toLowerCase().contains(text.toLowerCase());
        BooleanExpression byTextInDescr = QItem.item.description.toLowerCase().contains(text.toLowerCase());

        List<Item> foundItems = (List<Item>)itemRepository.findAll(byAvailable.and(byTextInName.or(byTextInDescr)));


//        List<Item> itemList = itemRepository.findAllByAvailableTrueIn(itemRepository.findAllByNameOrDescriptionContaining(text,text));
//        itemRepository.findByIdIn(List ids)
//        itemRepository.findAllby


//        assertThat(foundItems.size(),equalTo(items.size()));

//        assertItems(item, itemDto);
    }






















    @Data
    private static class ItemData {
        public final UserDto itemOwner;
        public final ItemDto item;
    }

    private ItemData makeItemDataStepByStep(Boolean available) {
        String itemOwnersName = "itemOwner" + itemOwners.size();
        String itemName = "item" + items.size();

        UserDto userDto = makeUserDto(itemOwnersName, itemOwnersName + "@email.com");
        UserDto itemOwnerUserDto = userService.createUser(userDto);
        Integer itemOwnerId = itemOwnerUserDto.getId();//=3
        itemOwners.add(itemOwnerId);


        ItemDto itemDto;
        if (available) {
            itemDto = makeAvailableItemDto(itemName, itemName + "...");
        } else {
            itemDto = makeNotAvailableItemDto(itemName, itemName + "...");
        }

        ItemDto itemDtoCreated = itemService.createItem(itemOwnerId, itemDto);
        Integer itemId = itemDtoCreated.getId();//=2
        items.add(itemId);


        ItemData data = new ItemData(itemOwnerUserDto, itemDtoCreated);
        return data;
    }

    private ItemDto makeAvailableItemDto(String name, String description) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(Boolean.TRUE);
        return dto;
    }

    private ItemDto makeNotAvailableItemDto(String name, String description) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(Boolean.FALSE);
        return dto;
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


    private static void assertJustCreatedItems(ItemDto itemDto, Item item) {
        assertAll(
                () -> assertThat(item.getId(), notNullValue()),
                () -> assertThat(item.getName(), equalTo(itemDto.getName())),
                () -> assertThat(item.getDescription(), equalTo(itemDto.getDescription())),
                () -> assertThat(item.getAvailable(), equalTo(Boolean.TRUE)),
                () -> assertThat(item.getItemRequest(), equalTo(null))
        );
    }

    private static void assertItems(Item item, ItemDto itemDto) {
        assertAll(
                () -> assertThat(item.getId(), equalTo(itemDto.getId())),
                () -> assertThat(item.getName(), equalTo(itemDto.getName())),
                () -> assertThat(item.getDescription(), equalTo(itemDto.getDescription())),
                () -> assertThat(item.getAvailable(), equalTo(itemDto.getAvailable())),
                () -> assertThat(item.getItemRequest().getId(), equalTo(itemDto.getRequestId()))
        );

    }

    private static void assertUsers(User user, UserDto ownerDto) {
        assertAll(
                () -> assertThat(user.getId(), equalTo(ownerDto.getId())),
                () -> assertThat(user.getName(), equalTo(ownerDto.getName())),
                () -> assertThat(user.getEmail(), equalTo(ownerDto.getEmail()))
        );
    }

    private static void assertItems(Integer userId, Integer itemId, ItemDtoForUpdate itemDtoForUpdate, Item updatedItem) {
        assertAll(
                () -> assertThat(updatedItem.getId(), equalTo(itemId)),
                () -> assertThat(updatedItem.getName(), equalTo(itemDtoForUpdate.getName())),
                () -> assertThat(updatedItem.getDescription(), equalTo(itemDtoForUpdate.getDescription())),
                () -> assertThat(updatedItem.getAvailable(), equalTo(itemDtoForUpdate.getAvailable())),
                () -> assertThat(updatedItem.getItemRequest(), equalTo(null)),
                () -> assertThat(updatedItem.getOwner().getId(), equalTo(userId))
        );
    }


}

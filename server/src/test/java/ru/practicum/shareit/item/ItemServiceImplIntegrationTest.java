package ru.practicum.shareit.item;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.BookingOutDto;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentItemDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.util.Constants.MAGIC_NUMBER;
import static ru.practicum.shareit.util.Constants.SORT_BY_ID_DESC;

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
    private final ItemRequestService itemRequestService;
    private final BookingService bookingService;

    private static List<Integer> itemOwners;
    private static List<Integer> requestOwners;

    private static List<Integer> items;
    private static List<Integer> requests;

    private static List<Integer> notOwners;
    private static List<Integer> bookingOwners;
    private static List<Integer> bookings;

    private static List<Integer> comments;

    private static List<Integer> commentOwners;

    @BeforeAll
    static void setup() {
        itemOwners = new ArrayList<>();
        items = new ArrayList<>();
        notOwners = new ArrayList<>();
        requests = new ArrayList<>();
        requestOwners = new ArrayList<>();
        bookingOwners = new ArrayList<>();
        bookings = new ArrayList<>();
        commentOwners = new ArrayList<>();
        comments = new ArrayList<>();
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
        ItemData itemData = makeItemDataByAvailable(true);
        Integer itemOwnerId = itemData.itemOwner.getId();
        Integer itemId = itemData.getItem().getId();

        ItemDto itemDto = itemService.getItem(itemOwnerId, itemId);

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));

        assertItemsWithoutRequests(itemDto, item);
    }

    @Order(31)
    @Test
    void getItem_whenUserIsNotFound_thenThrowNotFoundException() {
        ItemData itemData = makeItemDataByAvailable(true);
        Integer itemOwnerId = itemData.getItemOwner().getId();
        Integer itemId = itemData.getItem().getId();

        assertThrows(NotFoundException.class, () -> itemService.getItem(MAGIC_NUMBER, itemId));
    }

    @Order(32)
    @Test
    void getItem_whenItemIsNotFound_thenThrowNotFoundException() {
        ItemData itemData = makeItemDataByAvailable(true);
        Integer itemOwnerId = itemData.getItemOwner().getId();
        Integer itemId = itemData.getItem().getId();

        assertThrows(NotFoundException.class, () -> itemService.getItem(itemOwnerId, MAGIC_NUMBER));
    }


    @Order(40)
    @Test
    void getUserItemsWithBooking() {
        ItemDataWithBooking itemData =  makeItemWithBooking(true);
        Integer itemOwnerId = itemData.itemOwner.getId();
        Integer itemId = itemData.getItem().getId();

        List<ItemWithBookingDto> itemWithBookingDtos = itemService.getUserItemsWithBooking(itemOwnerId);

        List<Item> items = itemRepository.findAllByOwnerId(itemOwnerId, SORT_BY_ID_DESC);

        assertThat(items.size(), equalTo(itemWithBookingDtos.size()));
        //TODO:
    }


    @Order(50)
    @Test
    void getUserItems() {
        ItemData itemData = makeItemDataByAvailable(true);
        Integer itemOwnerId = itemData.itemOwner.getId();
        Integer itemId = itemData.getItem().getId();

        String text = "item";
        List<ItemDto> itemDtos = itemService.getUserItems(itemOwnerId, text);

        BooleanExpression byAvailable = QItem.item.available.eq(true);
        BooleanExpression byTextInName = QItem.item.name.toLowerCase().contains(text.toLowerCase());
        BooleanExpression byTextInDescr = QItem.item.description.toLowerCase().contains(text.toLowerCase());

        List<Item> foundItems = (List<Item>) itemRepository.findAll(byAvailable.and(byTextInName.or(byTextInDescr)));

        assertThat(foundItems.size(), equalTo(itemDtos.size()));

        for (int i = 0; i < itemDtos.size(); i++) {
            assertItemsWithoutRequests(itemDtos.get(i), foundItems.get(i));
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ItemData {
        private UserDto itemOwner;
        private ItemDto item;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ItemDataWithRequest {
        private UserDto itemOwner;
        private ItemDto item;
        private UserDto requester;
        private ItemRequestDto request;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ItemDataWithBooking {
        private UserDto itemOwner;
        private ItemDto item;
        private UserDto requester;
        private ItemRequestDto request;
        private UserDto commentOwner;
        private List<CommentItemDto> comments;
//        private ItemWithBookingDto itemWithBooking;

    }


    private ItemDataWithBooking makeItemWithBooking(Boolean available) {
        String itemOwnersName = "itemOwner" + itemOwners.size();
        String requestOwnerName = "requestOwner" + requestOwners.size();
        String itemName = "Item" + items.size();
        String itemRequestName = "Request" + requests.size();
        String bookingOwnerName = "bookingOwner" + requestOwners.size();

        UserDto requesterUserDto = makeUserDto(requestOwnerName, requestOwnerName + "@email.com");
        UserDto createdRequesterDto = userService.createUser(requesterUserDto);
        Integer requesterDtoId = createdRequesterDto.getId();
        requestOwners.add(requesterDtoId);

        ItemRequest itemRequest = makeItemRequest(itemRequestName);
        ItemRequestDto createdItemRequestDto = itemRequestService.create(requesterDtoId, itemRequest);
        Integer itemRequestDtoId = createdItemRequestDto.getId();
        requests.add(itemRequestDtoId);

        UserDto itemOwnerUserDto = makeUserDto(itemOwnersName, itemOwnersName + "@email.com");
        UserDto createdItemOwnerDto = userService.createUser(itemOwnerUserDto);
        Integer itemOwnerId = createdItemOwnerDto.getId();
        itemOwners.add(itemOwnerId);

        ItemDto iItemDtoWithItemRequest = makeItemDtoWithItemRequest(itemName, itemName + "...", available, itemRequestDtoId);
        ItemDto createdItemDto = itemService.createItem(itemOwnerId, iItemDtoWithItemRequest);
        Integer itemId = createdItemDto.getId();
        items.add(itemId);


        UserDto nextBookerUserDto = makeUserDto(bookingOwnerName, bookingOwnerName + "@email.com");
        UserDto createdNextBookerDto = userService.createUser(nextBookerUserDto);
        Integer nextBookerId = createdNextBookerDto.getId();
        bookingOwners.add(nextBookerId);

        LocalDateTime now = LocalDateTime.now();
        BookingDto nextBookingDto = makeBookingDto(now.plusDays(1), now.plusDays(2), itemId);
        BookingOutDto createdNextBooking = bookingService.createBooking(nextBookerId, nextBookingDto);
        Integer nextBookingId = createdNextBooking.getId();
        bookings.add(nextBookingId);
        log.info("Booking created by user with id " + nextBookerId
                + " for item with id " + itemId
                + " owned by user with id " + itemOwnerId);


        Integer pastBookerId = requesterDtoId;

        BookingDto pastBookingDto = makeBookingDto(now.minusDays(2), now.minusDays(1), itemId);
        BookingOutDto createdPastBooking = bookingService.createBooking(pastBookerId, pastBookingDto);
        Integer pastBookingId = createdPastBooking.getId();
        bookings.add(pastBookingId);
        log.info("Booking created by user with id " + pastBookerId
                + " for item with id " + itemId
                + " owned by user with id " + itemOwnerId);


        UserDto createdCommenterDto = createdRequesterDto;
        User commenter = UserMapper.mapToUser(createdCommenterDto);
        Comment comment = new Comment();
        comment.setText("Comment");
//        CommentItemDto commentItemDto = CommentMapper.mapToCommentItemDto(comment);
        CommentItemDto createdCommentDto = itemService.createItemComment(commenter.getId(), itemId, comment);
        Integer commentDtoId = createdCommentDto.getId();
        comments.add(commentDtoId);

//        ItemWithBookingDto itemDataWithBookingDto = new ItemWithBookingDto();
//        itemDataWithBookingDto.setId(itemId);
//        itemDataWithBookingDto.setName(createdItemDto.getName());
//        itemDataWithBookingDto.setDescription(createdItemDto.getDescription());
//        itemDataWithBookingDto.setAvailable(createdItemDto.getAvailable());
//        itemDataWithBookingDto.setLastBooking(new LastBooking(pastBookingId,pastBookerId));
//        itemDataWithBookingDto.setNextBooking(new NextBooking(nextBookingId,nextBookerId));
//        itemDataWithBookingDto.setItemRequest(ItemRequestMapper.mapToItemRequest(createdItemRequestDto,UserMapper.mapToUser(createdRequesterDto)));
//        itemDataWithBookingDto.setComments(List.of(createdCommentDto));


        ItemDataWithBooking itemDataWithBooking = new ItemDataWithBooking();
        itemDataWithBooking.setItemOwner(createdItemOwnerDto);
        itemDataWithBooking.setRequester(createdRequesterDto);
        itemDataWithBooking.setItem(createdItemDto);
        itemDataWithBooking.setRequest(createdItemRequestDto);
//        itemDataWithBooking.setItemWithBooking(itemDataWithBookingDto);
        itemDataWithBooking.setCommentOwner(createdCommenterDto);
        itemDataWithBooking.setComments(List.of(createdCommentDto));

        return itemDataWithBooking;
    }

    private ItemData makeItemDataByAvailable(Boolean available) {
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

        ItemData data;
//      data = new ItemData(itemOwnerUserDto, itemDtoCreated);
        data = new ItemData();
        data.setItem(itemDtoCreated);
        data.setItemOwner(itemOwnerUserDto);
        return data;
    }

    private ItemDataWithRequest makeItemDataWithItemRequest(Boolean available) {
        String itemOwnersName = "itemOwner" + itemOwners.size();
        String requestOwnerName = "requestOwner" + requestOwners.size();
        String itemName = "Item" + items.size();
        String itemRequestName = "Item Request" + requests.size();

        UserDto requesterUserDto = makeUserDto(requestOwnerName, requestOwnerName + "@email.com");
        UserDto requesterDto = userService.createUser(requesterUserDto);
        Integer requesterDtoId = requesterDto.getId();
        requestOwners.add(requesterDtoId);

        UserDto userDto = makeUserDto(itemOwnersName, itemOwnersName + "@email.com");
        UserDto itemOwnerUserDto = userService.createUser(userDto);
        Integer itemOwnerId = itemOwnerUserDto.getId();
        itemOwners.add(itemOwnerId);


        ItemRequest itemRequest = makeItemRequest(itemRequestName);
        ItemRequestDto itemRequestDto = itemRequestService.create(requesterDtoId, itemRequest);
        Integer itemRequestDtoId = itemRequestDto.getId();
        requests.add(itemRequestDtoId);

        ItemDto iItemDtoWithItemRequest = makeItemDtoWithItemRequest(itemName, itemName + "...", available, itemRequestDtoId);
        ItemDto itemDto = itemService.createItem(itemOwnerId, iItemDtoWithItemRequest);
        Integer itemId = itemDto.getId();
        items.add(itemId);

        ItemDataWithRequest itemDataWithRequest;
//         itemDataWithRequest = makeItemDataWithRequest(itemOwnerUserDto, itemDto, requesterDto, itemRequestDto);
        itemDataWithRequest = new ItemDataWithRequest();//(itemOwnerUserDto, itemDto, requesterDto, itemRequestDto);
        itemDataWithRequest.setItemOwner(itemOwnerUserDto);
        itemDataWithRequest.setItem(itemDto);
        itemDataWithRequest.setRequester(requesterDto);
        itemDataWithRequest.setRequest(itemRequestDto);
        return itemDataWithRequest;
    }

    private static Comment makeComment(Integer id, String text, User author, LocalDateTime created) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setText(text);
        comment.setAuthor(author);
        comment.setCreated(created);
        return comment;
    }

    private BookingDto makeBookingDto(LocalDateTime start, LocalDateTime end, Integer itemId) {
        BookingDto dto = new BookingDto();
        dto.setStart(start);
        dto.setEnd(end);
        dto.setItemId(itemId);
        return dto;
    }


    private static ItemDataWithRequest makeItemDataWithRequest(UserDto itemOwnerUserDto, ItemDto itemDto, UserDto requesterDto, ItemRequestDto itemRequestDto) {
        ItemDataWithRequest itemDataWithRequest = new ItemDataWithRequest();//(itemOwnerUserDto, itemDto, requesterDto, itemRequestDto);
        itemDataWithRequest.setItemOwner(itemOwnerUserDto);
        itemDataWithRequest.setItem(itemDto);
        itemDataWithRequest.setRequester(requesterDto);
        itemDataWithRequest.setRequest(itemRequestDto);
        return itemDataWithRequest;
    }

    private static ItemRequest makeItemRequest(String description) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(description);
        return itemRequest;
    }

    private ItemDto makeItemDto(String name, String description, Boolean available) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        return dto;
    }

    private ItemDto makeItemDtoWithItemRequest(String name, String description, Boolean available, Integer requestId) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        dto.setRequestId(requestId);
        return dto;
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

    private static void assertItems(ItemDto itemDto, Item item) {
        assertAll(
                () -> assertThat(itemDto.getId(), equalTo(item.getId())),
                () -> assertThat(itemDto.getName(), equalTo(item.getName())),
                () -> assertThat(itemDto.getDescription(), equalTo(item.getDescription())),
                () -> assertThat(itemDto.getAvailable(), equalTo(item.getAvailable())),
                () -> assertThat(itemDto.getRequestId(), equalTo(item.getItemRequest().getId()))
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

    private static void assertItemsWithoutRequests(ItemDto itemDto, Item item) {
        assertAll(
                () -> assertThat(itemDto.getId(), equalTo(item.getId())),
                () -> assertThat(itemDto.getName(), equalTo(item.getName())),
                () -> assertThat(itemDto.getDescription(), equalTo(item.getDescription())),
                () -> assertThat(itemDto.getAvailable(), equalTo(item.getAvailable()))
        );
    }


}

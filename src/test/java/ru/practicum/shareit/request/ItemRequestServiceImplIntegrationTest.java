package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static ru.practicum.shareit.util.Constants.SORT_BY_REQUEST_CREATED_DESC;

@Slf4j
@Transactional
@SpringBootTest(
        properties = "db.name=shareittest",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplIntegrationTest {

    private final ItemRequestService itemRequestService;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final UserService userService;

    private static ItemRequest makeItemRequest(String description) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(description);
        return itemRequest;
    }

    @Test
    void create() {
        ItemRequestData itemRequestData = makeSimpleItemRequestData();
        Integer itemRequestDtoId = itemRequestData.getItemRequestDto().getId();
        ItemRequestDto itemRequestDto = itemRequestData.itemRequestDto;
        Integer requsterId = itemRequestData.getRequestOwner().getId();

        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestDtoId).orElseThrow(() -> new NotFoundException("Item not found by id " + itemRequestDtoId));

        assertItemRequests(requsterId, itemRequestDto, itemRequest);
    }

    @Test
    void getListOfItemRequestsByUserId() {
        ItemRequestData itemRequestData = makeSimpleItemRequestData();
        Integer requsterId = itemRequestData.getRequestOwner().getId();

        List<ItemRequestDto> itemRequestDtos = itemRequestService.get(requsterId);

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterId(requsterId, SORT_BY_REQUEST_CREATED_DESC);

        assertThat(itemRequests.size(), equalTo(itemRequestDtos.size()));
        for (int i = 0; i < itemRequests.size(); i++) {
            assertItemRequests(requsterId, itemRequestDtos.get(i), itemRequests.get(i));
        }
    }

    @Test
    void getItemRequestById() {
        ItemRequestData itemRequestData = makeSimpleItemRequestData();
        Integer requsterId = itemRequestData.getRequestOwner().getId();
        Integer requestId = itemRequestData.getItemRequestDto().getId();

        ItemRequestDto itemRequestDto = itemRequestService.get(requsterId, requestId);

        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Item not found"));

        assertItemRequests(requsterId, itemRequestDto, itemRequest);
    }

    @Test
    void getAllListOfItemRequestsByUserId() {
        ItemRequestData itemRequestData = makeItemRequestData();
        Integer requsterId = itemRequestData.getRequestOwner().getId();
        Integer itemOwnerId = itemRequestData.getItemOwner().getId();

        Integer from = 0;
        Integer size = 20;
        List<ItemRequestDto> itemRequestDtos = itemRequestService.get(itemOwnerId, from, size);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, SORT_BY_REQUEST_CREATED_DESC);
        List<ItemRequest> itemRequests = itemRequestRepository.findAll(page).getContent();

        assertThat(itemRequests.size(), equalTo(itemRequestDtos.size()));
        for (int i = 0; i < itemRequests.size(); i++) {
            assertItemRequests(requsterId, itemRequestDtos.get(i), itemRequests.get(i));
        }
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ItemRequestData {
        private ItemRequestDto itemRequestDto;
        private ItemDto item;
        private UserDto itemOwner;
        private UserDto requestOwner;

    }

    private ItemRequestData makeSimpleItemRequestData() {

        UserDto requesterUserDto = makeUserDto("John Doe", "some@email.com");
        UserDto requesterDto = userService.createUser(requesterUserDto);
        Integer requesterDtoId = requesterDto.getId();

        ItemRequest itemRequest = makeItemRequest("Item request");
        ItemRequestDto itemRequestDto = itemRequestService.create(requesterDtoId, itemRequest);

        ItemDto itemDto = makeItemDto("Something", "Some thing");
        Integer itemRequestDtoId = itemRequestDto.getId();

        ItemRequestData itemRequestData = new ItemRequestData();
        itemRequestData.setItemRequestDto(itemRequestDto);
        itemRequestData.setRequestOwner(requesterDto);

        return itemRequestData;
    }

    private ItemRequestData makeItemRequestData() {

        UserDto requesterUserDto = makeUserDto("John Doe", "john@email.com");
        UserDto requesterDto = userService.createUser(requesterUserDto);
        Integer requesterDtoId = requesterDto.getId();

        UserDto itemOwnerUserDto = makeUserDto("Jane Doe", "jane@email.com");
        UserDto itemOwnerDto = userService.createUser(itemOwnerUserDto);
        Integer itemOwnerDtoId = itemOwnerDto.getId();

        ItemRequest itemRequest = makeItemRequest("Item request");
        ItemRequestDto itemRequestDto = itemRequestService.create(requesterDtoId, itemRequest);
        Integer itemRequestDtoId = itemRequestDto.getId();

        ItemDto iItemDtoForRequest = makeItemDtoForRequest("Something", "Some thing", itemRequestDtoId);
        ItemDto itemDto = itemService.createItem(itemOwnerDtoId, iItemDtoForRequest);


        ItemRequestData itemRequestData = new ItemRequestData();
        itemRequestData.setItemRequestDto(itemRequestDto);
        itemRequestData.setRequestOwner(requesterDto);
        itemRequestData.setItemOwner(itemOwnerDto);
        itemRequestData.setItem(itemDto);

        return itemRequestData;
    }


    private static void assertItemRequests(Integer savedUserDtoId, ItemRequestDto itemRequestDto, ItemRequest itemRequest) {
        assertAll(
                () -> assertThat(itemRequestDto.getId(), equalTo(itemRequest.getId())),
                () -> assertThat(itemRequestDto.getDescription(), equalTo(itemRequest.getDescription())),
                () -> assertThat(itemRequestDto.getCreated(), notNullValue()),
                () -> assertThat(savedUserDtoId, equalTo(itemRequest.getRequester().getId()))
        );
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

    private ItemDto makeItemDtoForRequest(String name, String description, Integer requestId) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(Boolean.TRUE);
        dto.setRequestId(requestId);
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

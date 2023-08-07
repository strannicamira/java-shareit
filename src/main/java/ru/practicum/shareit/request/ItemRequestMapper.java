package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    @Valid
    public static ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto, User user) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequester(user);
        itemRequest.setCreated(itemRequestDto.getCreated());
        return itemRequest;
    }

    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest, List<ItemDto> dtos) {
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
//                itemRequest.getRequester(),
                itemRequest.getCreated(),
                dtos
        );
        return itemRequestDto;
    }

//    public static List<ItemRequestDto> mapToItemRequestDto(Iterable<ItemRequest> itemRequests) {
//        List<ItemRequestDto> dtos = new ArrayList<>();
//        for (ItemRequest itemRequest : itemRequests) {
//            dtos.add(mapToItemRequestDto(itemRequest, itemRequest.get));
//        }
//        return dtos;
//    }

}

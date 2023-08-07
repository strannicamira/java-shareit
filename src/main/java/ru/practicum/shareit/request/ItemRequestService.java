package ru.practicum.shareit.request;


import ru.practicum.shareit.item.ItemDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(Integer userId, ItemRequest itemRequest);

    List<ItemRequestDto> get(Integer userId);

    ItemRequestDto get(Integer userId, Integer requestId);

    List<ItemRequestDto> get(Integer userId, Integer from, Integer size);
}

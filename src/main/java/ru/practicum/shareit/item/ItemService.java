package ru.practicum.shareit.item;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ItemService {

    @Transactional(readOnly = true)
    ItemDto getItem(Integer userId, Integer itemId);

    List<ItemDto> getUserItems(Integer userId);

    @Transactional(readOnly = true) //TODO: ?
    List<ItemDto> getUserItems(Integer userId, String text);

    @Transactional
    ItemDto addNewItem(Integer userId, Item item);

    ItemDto updateItem(Integer userId, ItemDto itemDto, Integer itemId);

    @Transactional
    void deleteItem(Integer userId, Integer itemId);

    @Transactional(readOnly = true)
    ItemWithBookingDto getItemWithBooking(Integer userId, Integer itemId);
}

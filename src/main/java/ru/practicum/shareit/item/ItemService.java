package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentItemDto;

import java.util.List;

public interface ItemService {

    ItemDto getItem(Integer userId, Integer itemId);

    List<ItemDto> getUserItems(Integer userId);

    List<ItemDto> getUserItems(Integer userId, String text);

    ItemDto addNewItem(Integer userId, Item item);

    ItemDto updateItem(Integer userId, ItemDto itemDto, Integer itemId);

    void deleteItem(Integer userId, Integer itemId);

    ItemWithBookingDto getItemWithBooking(Integer userId, Integer itemId);

    List<ItemWithBookingDto> getUserItemsWithBooking(Integer userId);

    CommentItemDto addNewItemComment(Integer userId, Integer itemId, Comment comment);
}

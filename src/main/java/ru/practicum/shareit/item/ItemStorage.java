package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemStorage {
    List<ItemDto> findAll(Integer userId);

    Item findById(Integer id);

    ItemDto create(User userId, Item itemDto);

    ItemDto update(Integer id, User userId, ItemDto itemDto);

    void deleteById(Integer id);

    List<ItemDto> findAll(String text, Integer userId);
}

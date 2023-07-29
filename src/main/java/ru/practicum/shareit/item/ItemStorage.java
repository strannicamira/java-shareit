package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemStorage {
    List<Item> findAll();

    Item findById(Integer id);

    ItemDto create(Integer userId, Item itemDto);

    ItemDto update(Integer id, Integer userId, ItemDto itemDto);

    void deleteById(Integer id);
}

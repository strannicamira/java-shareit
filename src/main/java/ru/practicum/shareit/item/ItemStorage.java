package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemStorage {
    List<Item> findAll();

    Item findById(Integer id);

    ItemDto create(Integer userId, ItemDto itemDto);

    ItemDto update(Integer id, ItemDto itemDto);

    void deleteById(Integer id);
}

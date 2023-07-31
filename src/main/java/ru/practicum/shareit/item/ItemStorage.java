package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemStorage {
    List<Item> findAll(Integer userId);

    Item findById(Integer id);

    Item create(User userId, Item item);

    Item update(Integer id, User userId, Item item);

    void deleteById(Integer id);

    List<Item> findAll(String text, Integer userId);
}

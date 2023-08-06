package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemStorage {
    Item getItems(Integer itemId);

    List<Item> getUserItems(Integer userId);

    List<Item> getItems(String text, Integer userId);

    Item addNewItem(User userId, Item item);

    Item update(Integer id, User userId, Item item);

    void deleteItem(Integer itemId);


}

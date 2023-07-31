package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Primary
@RequiredArgsConstructor
@Slf4j
public class ItemStorageImpl implements ItemStorage {

    private Map<Integer, Item> items = new ConcurrentHashMap<>();
    private Integer id = 0;

    @Override
    public List<Item> findAll(String text, Integer userId) {
        ArrayList<Item> userItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getAvailable() && !text.isEmpty() &&
                    (item.getName().toLowerCase().contains(text.toLowerCase(Locale.ROOT)) ||
                            item.getDescription().toLowerCase().contains(text.toLowerCase()))) {
                userItems.add(item);
            }
        }
        return userItems;
    }

    @Override
    public List<Item> findAll(Integer userId) {
        List<Item> userItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId().equals(userId)) {
                userItems.add(item);
            }
        }
        return userItems;
    }

    @Override
    public Item findById(Integer id) {
        return Optional.ofNullable(items.get(id)).orElseThrow(() -> new NotFoundException("Предмет не найден в списке."));
    }

    @Override
    public Item create(User user, Item item) {
        item.setId(++id);
        item.setOwner(user);
        items.put(id, item);
        return item;
    }

    @Override
    public Item update(Integer id, User user, Item item) {
        Item itemToBeUpdated = findById(id);
        if (itemToBeUpdated != null && !itemToBeUpdated.getOwner().getId().equals(user.getId())) {
            throw new NotOwnerException("Пользователь не владелец");
        }
        item.setId(id);
        item.setName(item.getName() == null ? itemToBeUpdated.getName() : item.getName());
        item.setDescription(item.getDescription() == null ? itemToBeUpdated.getDescription() : item.getDescription());
        item.setAvailable(item.getAvailable() == null ? itemToBeUpdated.getAvailable() : item.getAvailable());
        item.setItemRequest(item.getItemRequest() == null ? itemToBeUpdated.getItemRequest() : item.getItemRequest());
        item.setOwner(user);
        items.put(id, item);
        return item;
    }

    @Override
    public void deleteById(Integer id) {
        items.remove(id);
    }

}

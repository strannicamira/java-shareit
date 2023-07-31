package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    public List<Item> findAll(String text, Integer userId) {
        log.info("Search all items by matched text '{}'", text);
        return itemStorage.findAll(text, userId);
    }

    @Override
    public List<Item> findAll(Integer userId) {
        log.info("Search all items by user id {}", userId);
        return itemStorage.findAll(userId);
    }

    @Override
    public Item findById(Integer id) {
        log.info("Search all items by id {}", id);
        return itemStorage.findById(id);
    }

    @Override
    public Item create(User userId, Item itemDto) {
        log.info("Create item by user id {}", userId);
        return itemStorage.create(userId, itemDto);
    }

    @Override
    public Item update(Integer id, User userId, Item item) {
        log.info("Update item by id {}", id);
        return itemStorage.update(id, userId, item);
    }

    @Override
    public void deleteById(Integer id) {
        log.info("Delete item by id {}", id);
        itemStorage.deleteById(id);
    }
}

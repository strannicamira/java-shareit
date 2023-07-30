package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    public List<ItemDto> findAll(String text, Integer userId) {
        log.info("Search all items by matched text '{}'", text);
        return itemStorage.findAll(text, userId);
    }

    @Override
    public List<ItemDto> findAll(Integer userId) {
        log.info("Search all items by user id {}", userId);
        return itemStorage.findAll(userId);
    }

    @Override
    public Item findById(Integer id) {
        log.info("Search all items by id {}", id);
        return itemStorage.findById(id);
    }

    @Override
    public ItemDto create(Integer userId, Item itemDto) {
        log.info("Create item by user id {}", userId);
        return itemStorage.create(userId, itemDto);
    }

    @Override
    public ItemDto update(Integer id, Integer userId, ItemDto itemDto) {
        log.info("Update item by id {} with user id {}", id, userId);
        return itemStorage.update(id, userId, itemDto);
    }

    @Override
    public void deleteById(Integer id) {
        log.info("Delete item by id {}", id);
        itemStorage.deleteById(id);
    }
}

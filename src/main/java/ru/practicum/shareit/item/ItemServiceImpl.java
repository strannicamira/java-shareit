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

    @Override
    public List<Item> findAll() {
        return itemStorage.findAll();
    }

    @Override
    public Item findById(Integer id) {
        return itemStorage.findById(id);
    }

    @Override
    public ItemDto create(Integer userId, Item itemDto) {
        return itemStorage.create(userId, itemDto);
    }

    @Override
    public ItemDto update(Integer id, Integer userId, ItemDto itemDto) {
        return itemStorage.update(id, userId, itemDto);
    }

    @Override
    public void deleteById(Integer id) {
        itemStorage.deleteById(id);
    }
}

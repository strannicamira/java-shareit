package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateEmailFoundException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Primary
@RequiredArgsConstructor
@Slf4j
public class ItemStorageImpl implements ItemStorage {

    private Map<Integer, Item> items = new ConcurrentHashMap<>();
    private Integer id = 0;

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item findById(Integer id) {
        return Optional.ofNullable(items.get(id)).orElseThrow(() -> new NotFoundException("Пользователь не найден в списке."));
    }

    @Override
    public ItemDto create(Integer userId, ItemDto itemDto) {
        itemDto.setId(++id);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userId);
        items.put(id, item);
        return itemDto;
    }

    @Override
    public ItemDto update(Integer id, ItemDto itemDto) {
        Item item = findById(id);
        itemDto.setId(id);
        itemDto.setName(itemDto.getName() == null ? item.getName() : itemDto.getName());
        itemDto.setDescription(itemDto.getDescription() == null ? item.getDescription() : itemDto.getDescription());
        itemDto.setAvailable(itemDto.getAvailable() == null ? item.getAvailable() : itemDto.getAvailable());
        itemDto.setItemRequest(itemDto.getItemRequest() == null ? item.getItemRequest() : itemDto.getItemRequest());

        items.put(id, ItemMapper.toItem(itemDto));
        return itemDto;
    }

    @Override
    public void deleteById(Integer id) {
        items.remove(id);
    }
}

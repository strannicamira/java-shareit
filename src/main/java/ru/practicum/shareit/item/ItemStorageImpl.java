package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

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
    public List<ItemDto> findAll(String text, Integer userId) {
        ArrayList<ItemDto> userItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getAvailable() && !text.isEmpty() &&
                    (item.getName().toLowerCase().contains(text.toLowerCase(Locale.ROOT)) ||
                            item.getDescription().toLowerCase().contains(text.toLowerCase()))) {
                userItems.add(ItemMapper.toItemDto(item));
            }
        }
        return userItems;
    }

    @Override
    public List<ItemDto> findAll(Integer userId) {
        ArrayList<ItemDto> userItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().equals(userId)) {
                userItems.add(ItemMapper.toItemDto(item));
            }
        }
        return userItems;
    }

    @Override
    public Item findById(Integer id) {
        return Optional.ofNullable(items.get(id)).orElseThrow(() -> new NotFoundException("Предмет не найден в списке."));
    }

    @Override
    public ItemDto create(Integer userId, Item item) {
        item.setId(++id);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        item.setOwner(userId);
        items.put(id, item);
        return itemDto;
    }

    @Override
    public ItemDto update(Integer id, Integer userId, ItemDto itemDto) {
        Item item = findById(id);
        itemDto.setId(id);
        itemDto.setName(itemDto.getName() == null ? item.getName() : itemDto.getName());
        itemDto.setDescription(itemDto.getDescription() == null ? item.getDescription() : itemDto.getDescription());
        itemDto.setAvailable(itemDto.getAvailable() == null ? item.getAvailable() : itemDto.getAvailable());
        itemDto.setItemRequest(itemDto.getItemRequest() == null ? item.getItemRequest() : itemDto.getItemRequest());

        Item itemToPut = ItemMapper.toItem(itemDto);
        itemToPut.setOwner(userId);
        items.put(id, itemToPut);
        return itemDto;
    }

    @Override
    public void deleteById(Integer id) {
        items.remove(id);
    }

}

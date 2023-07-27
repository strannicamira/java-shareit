package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;

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
    public Item create(Item user) {
        return itemStorage.create(user);
    }

    @Override
    public ItemDto update(Integer id, ItemDto itemDto) {
        return itemStorage.update(id, itemDto);
    }

    @Override
    public void deleteById(Integer id) {
        itemStorage.deleteById(id);
    }
}

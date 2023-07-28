package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.DuplicateEmailFoundException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;


import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;

    @GetMapping
    public List<Item> findAll() {
        return itemService.findAll();
    }

    @GetMapping(value = "/{id}")
    public Item findById(@PathVariable Integer id) {
        return itemService.findById(id);
    }

    @PostMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Integer userId, @Valid @RequestBody ItemDto itemDto) {
//        if (userService.findById(userId) == null) {
//            throw new NotFoundException("Пользователь такой не существует");
//        }
        userService.findById(userId);
        return itemService.create(userId, itemDto);
    }

    @PatchMapping(value = "/{id}")
    public ItemDto update(@PathVariable("id") Integer id, @Valid @RequestBody ItemDto itemDto) {
        return itemService.update(id, itemDto);
    }

    @DeleteMapping(value = "/{itemId}")
    public void deleteById(@PathVariable(name = "itemId") Integer id) {
        itemService.deleteById(id);
    }

}

package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;


import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @GetMapping(value = "/search")
    public List<ItemDto> searchItem(@RequestParam("text") String text, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.findAll(text, userId).stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.findAll(userId).stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }

    @GetMapping(value = "/{itemId}")
    public ItemDto findById(@PathVariable Integer itemId, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemMapper.toItemDto(itemService.findById(itemId));
    }

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Integer userId, @Valid @RequestBody Item itemDto) {
        return itemMapper.toItemDto(itemService.create(userService.findById(userId), itemDto));
    }

    @PatchMapping(value = "/{id}")
    public ItemDto update(@PathVariable("id") Integer id, @RequestHeader("X-Sharer-User-Id") Integer userId, @Valid @RequestBody ItemDto itemDto) {
        return itemMapper.toItemDto(itemService.update(id, userService.findById(userId), itemMapper.toItem(itemDto)));
    }

    @DeleteMapping(value = "/{itemId}")
    public void deleteById(@PathVariable(name = "itemId") Integer id) {
        itemService.deleteById(id);
    }

}

package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping(value = "/{itemId}")
    public ItemDto get(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer itemId) { // TODO: Check userId?
        return itemService.getItem(userId, itemId);
    }

    @GetMapping(value = "/search")
    public List<ItemDto> get(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(name = "text", required = false) String text) {
        if (text == null || text.isEmpty()) {
            return itemService.getUserItems(userId);
        } else {
            return itemService.getUserItems(userId, text);
        }
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Later-User-Id") Integer userId, @RequestBody ItemDto itemDto) {
        return itemService.addNewItem(userId, itemDto);
    }

    @PatchMapping(value = "/{id}")//TODO:?
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Integer userId, @Valid @RequestBody ItemDto itemDto, @PathVariable("id") Integer itemId) {
//        return itemService.addNewItem(userId, itemDto); // TODO: Check itemId?
        return itemService.updateItem(userId, itemDto, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Later-User-Id") Integer userId, @PathVariable(name = "itemId") Integer itemId) {
        itemService.deleteItem(userId, itemId);
    }
}

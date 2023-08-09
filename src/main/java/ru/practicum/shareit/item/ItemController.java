package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentItemDto;


import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping(value = "/{itemId}")
    public ItemWithBookingDto get(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer itemId) { // TODO: Check userId?
        return itemService.getItemWithBooking(userId, itemId);
    }

    @GetMapping()
    public List<ItemWithBookingDto> get(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getUserItemsWithBooking(userId);
    }

    @GetMapping(value = "/search")
    public List<ItemDto> get(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(name = "text", required = false) String text) {
        return itemService.getUserItems(userId, text);
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Integer userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping(value = "/{id}")//TODO:?
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Integer userId, @Valid @RequestBody ItemDtoForUpdate itemDto, @PathVariable("id") Integer itemId) {
        return itemService.updateItem(userId, itemDto, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable(name = "itemId") Integer itemId) {
        itemService.deleteItem(userId, itemId);
    }


    @PostMapping("/{itemId}/comment")
    public CommentItemDto add(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @PathVariable(name = "itemId") Integer itemId,
                              @Valid @RequestBody Comment comment) {
        return itemService.createItemComment(userId, itemId, comment);
    }
}

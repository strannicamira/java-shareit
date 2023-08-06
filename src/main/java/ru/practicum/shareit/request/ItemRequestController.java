package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentItemDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemWithBookingDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto add(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @Valid @RequestBody ItemRequest itemRequest) {
        return itemRequestService.create(userId, itemRequest);
    }

    @GetMapping()
    public List<ItemRequestDto> get(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.get(userId);
    }

    @GetMapping(value = "/{requestId}")
    public ItemRequestDto get(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @PathVariable Integer requestId) {
        return itemRequestService.get(userId, requestId);
    }

    @GetMapping(value = "/all")
    public List<ItemDto> get(@RequestHeader("X-Sharer-User-Id") Integer userId,
                             @RequestParam(name = "from", required = false) Integer from,
                             @RequestParam(name = "size", required = false) Integer size) {
        return itemRequestService.get(userId, from, size);
    }

}

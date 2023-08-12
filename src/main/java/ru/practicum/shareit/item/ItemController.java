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

    /*
    Добавление новой вещи.
    Будет происходить по эндпойнту POST /items.
    На вход поступает объект ItemDto. userId в заголовке X-Sharer-User-Id —
    это идентификатор пользователя, который добавляет вещь. И
    менно этот пользователь — владелец вещи.
    Идентификатор владельца будет поступать на вход в каждом из запросов, рассмотренных далее.
     */
    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    /*
    Редактирование вещи. Эндпойнт PATCH /items/{itemId}.
    Изменить можно название, описание и статус доступа к аренде.
    Редактировать вещь может только её владелец.
     */
    @PatchMapping(value = "/{id}")//TODO:?
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @Valid @RequestBody ItemDtoForUpdate itemDto, @PathVariable("id") Integer itemId) {
        return itemService.updateItem(userId, itemDto, itemId);
    }

    /*
    Просмотр информации о конкретной вещи по её идентификатору.
    Эндпойнт GET /items/{itemId}.
    Информацию о вещи может просмотреть любой пользователь.
     */
    @GetMapping(value = "/{itemId}")
    public ItemWithBookingDto getItemWithBooking(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer itemId) { // TODO: Check userId?
        return itemService.getItemWithBooking(userId, itemId);
    }

    /*
    Просмотр владельцем списка всех его вещей
    с указанием названия и описания для каждой.
    Эндпойнт GET /items.


    Осталась пара штрихов. Итак, вы добавили возможность бронировать вещи.
    Теперь нужно, чтобы владелец видел даты последнего
    и ближайшего следующего бронирования для каждой вещи,
    когда просматривает список (GET /items).
     */
    @GetMapping()
    public List<ItemWithBookingDto> getUserItemsWithBooking(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getUserItemsWithBooking(userId);
    }

    /*
    Поиск вещи потенциальным арендатором.
    Пользователь передаёт в строке запроса текст,
    и система ищет вещи, содержащие этот текст в названии или описании.
    Происходит по эндпойнту /items/search?text={text},
    в text передаётся текст для поиска.
    Проверьте, что поиск возвращает только доступные для аренды вещи.
     */
    @GetMapping(value = "/search")
    public List<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(name = "text", required = false) String text) {
        return itemService.getUserItems(userId, text);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable(name = "itemId") Integer itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentItemDto createItemComment(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @PathVariable(name = "itemId") Integer itemId,
                              @Valid @RequestBody Comment comment) {
        return itemService.createItemComment(userId, itemId, comment);
    }
}

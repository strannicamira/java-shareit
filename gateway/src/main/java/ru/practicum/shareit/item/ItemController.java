package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoToCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoToUpdate;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    /*
    Добавление новой вещи.
    Будет происходить по эндпойнту POST /items.
    На вход поступает объект ItemDto. userId в заголовке X-Sharer-User-Id —
    это идентификатор пользователя, который добавляет вещь. И
    менно этот пользователь — владелец вещи.
    Идентификатор владельца будет поступать на вход в каждом из запросов, рассмотренных далее.
     */
    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @Valid @RequestBody ItemDto itemDto) {
        return itemClient.createItem(userId, itemDto);
    }

    /*
    Редактирование вещи. Эндпойнт PATCH /items/{itemId}.
    Изменить можно название, описание и статус доступа к аренде.
    Редактировать вещь может только её владелец.
     */
    @PatchMapping(value = "/{id}")//TODO:?
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @Valid @RequestBody ItemDtoToUpdate itemDto,
                                             @PathVariable("id") Integer itemId) {
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    /*
    Просмотр информации о конкретной вещи по её идентификатору.
    Эндпойнт GET /items/{itemId}.
    Информацию о вещи может просмотреть любой пользователь.
     */
    @GetMapping(value = "/{itemId}")
    public ResponseEntity<Object> getItemWithBookingById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                         @PathVariable Integer itemId) {
        return itemClient.getItemWithBookingById(userId, itemId);
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
    public ResponseEntity<Object> getAllItemsWithBookingByOwner(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemClient.getAllItemsWithBookingByOwner(userId);
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
    public ResponseEntity<Object> getAllItemsByAnyUserByText(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                    @RequestParam(name = "text", required = false) String text) {
        return itemClient.getAllItemsByAnyUserByText(userId, text);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                           @PathVariable(name = "itemId") Integer itemId) {
        itemClient.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createItemComment(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                            @PathVariable(name = "itemId") Integer itemId,
                                            @Valid @RequestBody CommentDtoToCreate commentDto) {
        return itemClient.createItemComment(userId, itemId, commentDto);
    }
}

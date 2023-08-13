package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    /*
    POST /requests — добавить новый запрос вещи.
    Основная часть запроса — текст запроса,
    где пользователь описывает, какая именно вещь ему нужна.
     */
    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                         @Valid @RequestBody ItemRequestDto itemRequest) {
        return itemRequestClient.create(userId, itemRequest);
    }

    /*
    GET /requests — получить список своих запросов вместе с данными об ответах на них.
    Для каждого запроса должны указываться описание, дата и время создания и список ответов в формате:
    id вещи, название, её описание description,
    а также requestId запроса и признак доступности вещи available.
    Так в дальнейшем, используя указанные id вещей,
    можно будет получить подробную информацию о каждой вещи.
    Запросы должны возвращаться в отсортированном порядке от более новых к более старым.
     */
    @GetMapping()
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestClient.get(userId);
    }

    /*
    GET /requests/{requestId} — получить данные об одном конкретном запросе вместе с данными
     об ответах на него в том же формате, что и в эндпоинте GET /requests.
    Посмотреть данные об отдельном запросе может любой пользователь.
     */
    @GetMapping(value = "/{requestId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                      @PathVariable Integer requestId) {
        return itemRequestClient.get(userId, requestId);
    }

    /*
    GET /requests/all?from={from}&size={size} —
    получить список запросов, созданных другими пользователями.
    С помощью этого эндпоинта пользователи смогут просматривать существующие запросы,
    на которые они могли бы ответить. Запросы сортируются по дате создания:
    от более новых к более старым. Результаты должны возвращаться постранично.
    Для этого нужно передать два параметра:
    from — индекс первого элемента, начиная с 0, и size — количество элементов для отображения.

    Теперь вернёмся к улучшению, о котором мы упомянули ранее.
    Вы уже используете в запросе GET /requests/all пагинацию,
    поскольку запросов может быть очень много.
     */
    @GetMapping(value = "/all")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                      @Positive @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        return itemRequestClient.get(userId, from, size);
    }

}

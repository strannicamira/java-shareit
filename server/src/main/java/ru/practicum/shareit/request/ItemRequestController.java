package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    /*
    POST /requests — добавить новый запрос вещи.
    Основная часть запроса — текст запроса,
    где пользователь описывает, какая именно вещь ему нужна.
     */
    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                            @Valid @RequestBody ItemRequest itemRequest) {
        return itemRequestService.create(userId, itemRequest);
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
    public List<ItemRequestDto> get(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.get(userId);
    }

    /*
    GET /requests/all?from={from}&size={size} —
    получить список запросов, созданных другими пользователями.
    С помощью этого эндпоинта пользователи смогут просматривать существующие запросы,
    на которые они могли бы ответить. Запросы сортируются по дате создания:
    от более новых к более старым. Результаты должны возвращаться постранично.
    Для этого нужно передать два параметра:
    from — индекс первого элемента, начиная с 0, и size — количество элементов для отображения.
     */
    @GetMapping(value = "/{requestId}")
    public ItemRequestDto get(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @PathVariable Integer requestId) {
        return itemRequestService.get(userId, requestId);
    }

    /*
    GET /requests/{requestId} — получить данные об одном конкретном запросе вместе с данными
     об ответах на него в том же формате, что и в эндпоинте GET /requests.
    Посмотреть данные об отдельном запросе может любой пользователь.


    Теперь вернёмся к улучшению, о котором мы упомянули ранее.
    Вы уже используете в запросе GET /requests/all пагинацию,
    поскольку запросов может быть очень много.
     */
    @GetMapping(value = "/all")
    public List<ItemRequestDto> get(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                    @RequestParam(name = "from", required = false) Integer from,
                                    @RequestParam(name = "size", required = false) Integer size) {
        return itemRequestService.get(userId, from, size);
    }

}

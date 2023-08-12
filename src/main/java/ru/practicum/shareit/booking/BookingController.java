package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    /*
    (1)
    Добавление нового запроса на бронирование.
    Запрос может быть создан любым пользователем,
    а затем подтверждён владельцем вещи. Эндпоинт — POST /bookings.
    После создания запрос находится в статусе WAITING — «ожидает подтверждения».
     */
    @PostMapping
    public BookingOutDto create(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    /*
    (2)
    Подтверждение или отклонение запроса на бронирование.
    Может быть выполнено только владельцем вещи.
    Затем статус бронирования становится либо APPROVED, либо REJECTED.
    Эндпоинт — PATCH /bookings/{bookingId}?approved={approved},
    параметр approved может принимать значения true или false.
     */
    @PatchMapping(value = "/{bookingId}")
    public BookingOutDto update(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                @PathVariable("bookingId") Integer bookingId,
                                @RequestParam(name = "approved") Boolean approved) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    /*
    (3)
    Получение данных о конкретном бронировании (включая его статус).
    Может быть выполнено либо автором бронирования,
    либо владельцем вещи, к которой относится бронирование.
    Эндпоинт — GET /bookings/{bookingId}.
     */
    @GetMapping(value = "/{bookingId}")
    public BookingOutDto getBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                    @PathVariable Integer bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }


    /*
    (4)
    Получение списка всех бронирований текущего пользователя.
    Эндпоинт — GET /bookings?state={state}.
    Параметр state необязательный и по умолчанию равен ALL (англ. «все»).
    Также он может принимать значения CURRENT (англ. «текущие»), **PAST** (англ. «завершённые»),
    FUTURE (англ. «будущие»), WAITING (англ. «ожидающие подтверждения»), REJECTED (англ. «отклонённые»).
    Бронирования должны возвращаться отсортированными по дате от более новых к более старым.
     */
    @GetMapping()
    public List<BookingOutDto> getUserBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                          @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
                                          @RequestParam(name = "from", required = false) Integer from,
                                          @RequestParam(name = "size", required = false) Integer size) {
        return bookingService.getUserBookings(userId, state, from, size);
    }

    /*
    (5)
    Получение списка бронирований для всех вещей текущего пользователя.
    Эндпоинт — GET /bookings/owner?state={state}. Этот запрос имеет смысл для владельца хотя бы одной вещи.
    Работа параметра state аналогична его работе в предыдущем сценарии.
     */
    @GetMapping(value = "/owner")
    public List<BookingOutDto> getItemsBookings(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                    @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
                                                    @RequestParam(name = "from", required = false) Integer from,
                                                    @RequestParam(name = "size", required = false) Integer size) {
        return bookingService.getItemsBookings(userId, state, from, size);
    }

    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @PathVariable(name = "bookingId") Integer bookingId) {
        bookingService.deleteBooking(userId, bookingId);
    }
}


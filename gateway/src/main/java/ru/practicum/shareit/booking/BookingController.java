package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    /*
    (1)
    Добавление нового запроса на бронирование.
    Запрос может быть создан любым пользователем,
    а затем подтверждён владельцем вещи. Эндпоинт — POST /bookings.
    После создания запрос находится в статусе WAITING — «ожидает подтверждения».
     */
    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                @Valid @RequestBody BookingDto bookingDto) {
        return bookingClient.createBooking(userId, bookingDto);
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
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                         @PathVariable("bookingId") Integer bookingId,
                                         @RequestParam(name = "approved") Boolean approved) {
        return bookingClient.updateBooking(userId, bookingId, approved);
    }

    /*
    (3)
    Получение данных о конкретном бронировании (включая его статус).
    Может быть выполнено либо автором бронирования,
    либо владельцем вещи, к которой относится бронирование.
    Эндпоинт — GET /bookings/{bookingId}.
     */
    @GetMapping(value = "/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @PathVariable Integer bookingId) {
        return bookingClient.getBooking(userId, bookingId);
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
    //TODO:
    @GetMapping()
    public ResponseEntity<Object> getUserBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                 @RequestParam(name = "state", required = false, defaultValue = "ALL") String stringState,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
//        BookingState state = BookingState.from(stringState)
//                .orElseThrow(() -> new IllegalStateException("Unknown state: UNSUPPORTED_STATUS"));
        return bookingClient.getUserBookings(userId, stringState, from, size);
    }

    /*
    (5)
    Получение списка бронирований для всех вещей текущего пользователя.
    Эндпоинт — GET /bookings/owner?state={state}. Этот запрос имеет смысл для владельца хотя бы одной вещи.
    Работа параметра state аналогична его работе в предыдущем сценарии.
     */
    //TODO:
    @GetMapping(value = "/owner")
    public ResponseEntity<Object> getItemsBookings(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                   @RequestParam(name = "state", required = false, defaultValue = "ALL") String stringState,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
//        BookingState state = BookingState.from(stringState)
//                .orElseThrow(() -> new IllegalStateException("Unknown state: UNSUPPORTED_STATUS"));
        return bookingClient.getItemsBookings(userId, stringState, from, size);
    }

    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @PathVariable(name = "bookingId") Integer bookingId) {
        bookingClient.deleteBooking(userId, bookingId);
    }
}


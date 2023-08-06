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

    @PostMapping
    public BookingOutDto add(@RequestHeader("X-Sharer-User-Id") Integer userId,
                             @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping(value = "/{bookingId}")
    public BookingOutDto update(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                @PathVariable("bookingId") Integer bookingId,
                                @RequestParam(name = "approved") Boolean approved) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping(value = "/{bookingId}")
    public BookingOutDto get(@RequestHeader("X-Sharer-User-Id") Integer userId,
                             @PathVariable Integer bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }


    @GetMapping()
    public List<BookingOutDto> get(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                   @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping(value = "/owner")
    public List<BookingOutDto> getUserItemsBookings(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                    @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        return bookingService.getUserItemsBookings(userId, state);
    }

    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @PathVariable(name = "bookingId") Integer bookingId) {
        bookingService.deleteBooking(userId, bookingId);
    }
}


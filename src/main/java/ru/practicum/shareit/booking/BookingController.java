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
//            (consumes = {"application/json;charset=UTF-8"}, produces = {"application/json;charset=UTF-8"})
    public BookingOutDto add(@RequestHeader("X-Sharer-User-Id") Integer userId,//TODO: any (existed) user
                          @Valid @RequestBody BookingDto bookingDto) {//TODO: status DEFAULT WAITING
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping(value = "/{bookingId}")//TODO:?
    public BookingOutDto update(@RequestHeader("X-Sharer-User-Id") Integer userId,//TODO: check only by owner
                             @PathVariable("bookingId") Integer bookingId,
                             @RequestParam(name = "approved") Boolean approved) {//TODO: true or false ==> status APPROVED or REJECTED
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping(value = "/{bookingId}")
    public BookingOutDto get(@RequestHeader("X-Sharer-User-Id") Integer userId,//TODO: only by item owner or booking booker
                          @PathVariable Integer bookingId) { // TODO: Check userId?
        return bookingService.getBooking(userId, bookingId);
    }


    @GetMapping()
    public List<BookingOutDto> get(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {//TODO: state DEFAULT ALL
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


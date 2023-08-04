package ru.practicum.shareit.booking;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;

import java.util.List;

@Transactional(readOnly = true)
public interface BookingService {
    @Transactional
    BookingOutDto createBooking(Integer userId, BookingDto bookingDto);

    @Transactional
    BookingOutDto updateBooking(Integer userId, Integer bookingId, Boolean approved);

    @Transactional(readOnly = true)
    BookingOutDto getBooking(Integer userId, Integer bookingId);

    @Transactional(readOnly = true)
    List<BookingOutDto> getUserBookings(Integer userId, String state);

    List<BookingOutDto> getUserItemsBookings(Integer userId, String state);

    List<BookingOutDto> getItemsBookingsByUser(Integer item, Integer userId, String state);

    @Transactional
    void deleteBooking(Integer userId, Integer bookingId);

    LastBooking getUserItemsLastPastBookings(Integer userId, Item item);

    NextBooking getUserItemsFutureNextBookings(Integer userId, Item item);
}

package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.Item;

import java.util.List;

public interface BookingService {
    BookingOutDto createBooking(Integer userId, BookingDto bookingDto);

    BookingOutDto updateBooking(Integer userId, Integer bookingId, Boolean approved);

    BookingOutDto getBooking(Integer userId, Integer bookingId);

    List<BookingOutDto> getUserBookings(Integer userId, String state, Integer from, Integer size);

    List<BookingOutDto> getUserItemsBookings(Integer userId, String state, Integer from, Integer size);

    List<BookingOutDto> getItemsBookingsByUser(Integer item, Integer userId, String state);

    void deleteBooking(Integer userId, Integer bookingId);

    LastBooking getUserItemsLastPastBookings(Integer userId, Item item);

    NextBooking getUserItemsFutureNextBookings(Integer userId, Item item);
}

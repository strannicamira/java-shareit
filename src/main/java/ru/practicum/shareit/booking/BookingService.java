package ru.practicum.shareit.booking;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface BookingService {
    @Transactional
    BookingDto createBooking(Integer userId, BookingDto bookingDtoooking);

    @Transactional
    BookingDto updateBooking(Integer userId, Integer bookingId, Boolean approved);

    @Transactional(readOnly = true)
    BookingDto getBooking(Integer userId, Integer bookingId);

    @Transactional(readOnly = true)
        //TODO: ?
    List<BookingDto> getUserBookings(Integer userId, String state);

    List<BookingDto> getUserItemsBookings(Integer userId, String state);

    @Transactional
    void deleteBooking(Integer userId, Integer bookingId);
}

package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.ItemBookingDto;
import ru.practicum.shareit.user.UserBookingDto;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingOutMapper {
    public static BookingOutDto mapToBookingOutDto(Booking booking) {
        return new BookingOutDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                new ItemBookingDto(booking.getItem().getId(), booking.getItem().getName()),
                new UserBookingDto(booking.getBooker().getId()),
                booking.getStatus()
        );
    }

    public static List<BookingOutDto> mapToBookingOutDto(Iterable<Booking> bookings) {
        List<BookingOutDto> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(mapToBookingOutDto(booking));
        }
        return dtos;
    }

}

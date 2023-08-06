package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemBookingDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserBookingDto;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingOutMapper {
    @Valid
    public static Booking mapToBooking(BookingOutDto bookingOutDto, Item item, User user) {
        Booking booking = new Booking();
        booking.setId(bookingOutDto.getId());
        booking.setStart(bookingOutDto.getStart());
        booking.setEnd(bookingOutDto.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(bookingOutDto.getStatus());
        return booking;
    }

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

package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemBookingDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserBookingDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingOutGetMapper {
    @Valid
    public static Booking mapToBooking(BookingOutDto bookingOutDto, Item item, User user, LocalDateTime start, LocalDateTime end) {
        Booking booking = new Booking();
        booking.setId(bookingOutDto.getId());
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(bookingOutDto.getStatus());
        return booking;
    }

    public static BookingOutGetDto mapToBookingOutGetDto(Booking booking) {
        return new BookingOutGetDto(
                booking.getId(),
                new ItemBookingDto(booking.getItem().getId(), booking.getItem().getName()),
                new UserBookingDto(booking.getBooker().getId()),
                booking.getStatus()
        );
    }

    public static List<BookingOutGetDto> mapToBookingOutGetDto(Iterable<Booking> bookings) {
        List<BookingOutGetDto> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(mapToBookingOutGetDto(booking));
        }
        return dtos;
    }

}

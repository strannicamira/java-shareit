package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.user.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    @Valid
    public static Booking mapToBooking(BookingDto bookingDto, User user) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId()); //TODO: ? https://github.com/praktikum-java/module4_spring_without_boot/blob/repositories/src/main/java/ru/practicum/booking/BookingMapper.java
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(bookingDto.getItem());
        booking.setBooker(user);
        booking.setBookingStatus(bookingDto.getBookingStatus());
        return booking;
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getBookingStatus()
        );
    }

    public static List<BookingDto> mapToBookingDto(Iterable<Booking> bookings) {
        List<BookingDto> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(mapToBookingDto(booking));
        }
        return dtos;
    }

}

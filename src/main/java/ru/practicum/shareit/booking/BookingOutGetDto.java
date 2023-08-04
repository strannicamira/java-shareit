package ru.practicum.shareit.booking;

import lombok.*;
import ru.practicum.shareit.item.ItemBookingDto;
import ru.practicum.shareit.user.UserBookingDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingOutGetDto {
    private Integer id;
    @ToString.Exclude
    private LocalDateTime start;
    @ToString.Exclude
    private LocalDateTime end;
    private ItemBookingDto item;
    private UserBookingDto booker;
    private BookingStatus status;

    public BookingOutGetDto(Integer id, ItemBookingDto item, UserBookingDto booker, BookingStatus status) {
        this.id = id;
        this.item = item;
        this.booker = booker;
        this.status = status;
    }
}

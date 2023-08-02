package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Integer id;
//    @DateTimeFormat(pattern = TIME_PATTERN)
    private LocalDate start;
//    @DateTimeFormat(pattern = TIME_PATTERN)
    private LocalDate end;
    private Item item;
    private User booker;
    private BookingStatus bookingStatus;
}

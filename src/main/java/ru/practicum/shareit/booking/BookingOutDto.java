package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.item.ItemBookingDto;
import ru.practicum.shareit.user.UserBookingDto;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.shareit.util.Constants.TIME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingOutDto {
    private Integer id;

    @NotNull
    @FutureOrPresent
//    @JsonFormat(pattern = TIME_PATTERN)
    @DateTimeFormat(pattern = TIME_PATTERN)
    private LocalDateTime start;

    @NotNull
    @FutureOrPresent
//    @JsonFormat(pattern = TIME_PATTERN)
    @DateTimeFormat(pattern = TIME_PATTERN)
    private LocalDateTime end;

    private ItemBookingDto item;
    private UserBookingDto booker;
    private BookingStatus status;
}

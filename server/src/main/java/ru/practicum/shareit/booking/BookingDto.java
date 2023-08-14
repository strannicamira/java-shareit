package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static ru.practicum.shareit.util.Constants.TIME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Integer id;

//    @NotNull
//    @FutureOrPresent
    @DateTimeFormat(pattern = TIME_PATTERN)
    private LocalDateTime start;

//    @NotNull
//    @FutureOrPresent
    @DateTimeFormat(pattern = TIME_PATTERN)
    private LocalDateTime end;

    private Integer itemId;
    private Integer bookerId;
    private BookingStatus status;
}

package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
//    private Integer id;

    private Integer itemId;
    //    private Integer bookerId;
//    private BookingStatus status;

    @NotNull
    @FutureOrPresent
//    @DateTimeFormat(pattern = TIME_PATTERN)
    private LocalDateTime start;

    @NotNull
    @FutureOrPresent
//    @DateTimeFormat(pattern = TIME_PATTERN)
    private LocalDateTime end;

}

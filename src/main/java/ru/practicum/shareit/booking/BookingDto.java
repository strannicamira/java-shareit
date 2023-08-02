package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.util.Constants;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Integer id;
//    @DateTimeFormat(pattern = Constants.TIME_PATTERN)
    private LocalDate start;
    //    @DateTimeFormat(pattern = TIME_PATTERN)
//    @FutureOrPresent
    private LocalDate end;
    private Integer itemId;
    private Integer bookerId;
    private BookingStatus bookingStatus;
}

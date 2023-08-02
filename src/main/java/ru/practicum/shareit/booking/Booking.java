package ru.practicum.shareit.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import static ru.practicum.shareit.util.Constants.TIME_PATTERN;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings", schema = "public")//TODO: @Table is optional, but check name that should be the same
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @FutureOrPresent
    @JsonFormat(pattern = TIME_PATTERN)
    @Column(name = "start_date")
    private LocalDate start;


//    @FutureOrPresent //TODO: instead check in createBooking
    @NotNull
    @FutureOrPresent
    @JsonFormat(pattern = TIME_PATTERN)
    @Column(name = "end_date")
    private LocalDate end;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id")
    private User booker;

    @Enumerated(EnumType.ORDINAL) //TODO: EnumType.ORDINAL
//    @Column(name = "status")
    private BookingStatus bookingStatus;
}

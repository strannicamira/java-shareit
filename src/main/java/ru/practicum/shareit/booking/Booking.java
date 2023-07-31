package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
//@Table(name = "bookings", schema = "public")
public class Booking {
    @Id
    private Integer id;
//    @NotEmpty
//    private String name;
    private LocalDate start;
    private LocalDate end;
//    private String description;

    @Transient // TODO: tmp
    private Item item;
    @Transient // TODO: tmp
    private User booker;
//    private ItemRequest request;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;
}

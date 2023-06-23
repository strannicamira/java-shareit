package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
public class Booking {
    private Integer id;
    @NotEmpty
    private String name;
    private LocalDate start;
    private LocalDate end;
    private String description;
    private Item item;
    private User booker;
    private ItemRequest request;
}

package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.LastBooking;
import ru.practicum.shareit.booking.NextBooking;
import ru.practicum.shareit.comment.CommentItemDto;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemWithBookingDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private LastBooking lastBooking;
    private NextBooking nextBooking;
    private ItemRequest itemRequest;
    private List<CommentItemDto> comments;
}

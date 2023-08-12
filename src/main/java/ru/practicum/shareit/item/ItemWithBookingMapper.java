package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.LastBooking;
import ru.practicum.shareit.booking.NextBooking;
import ru.practicum.shareit.comment.CommentItemDto;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemWithBookingMapper {
    public static ItemWithBookingDto mapToItemWithBookingDto(Item item, LastBooking lastBooking, NextBooking nextBooking, List<CommentItemDto> comments) {
        return new ItemWithBookingDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking,
                item.getItemRequest(),
                comments
        );
    }
}

package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.LastBooking;
import ru.practicum.shareit.booking.NextBooking;
import ru.practicum.shareit.comment.CommentItemDto;
import ru.practicum.shareit.user.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemWithBookingMapper {
    @Valid
    public static Item mapToItem(ItemWithBookingDto itemDto, User user) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        item.setItemRequest(itemDto.getItemRequest());
        return item;
    }

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

package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.LastBooking;
import ru.practicum.shareit.booking.NextBooking;
import ru.practicum.shareit.user.User;

import javax.validation.Valid;

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

    public static ItemWithBookingDto mapToItemWithBookingDto(Item item, LastBooking lastBooking, NextBooking nextBooking) {
        return new ItemWithBookingDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking,
                item.getItemRequest()
        );
    }

//    public static List<ItemWithBookingDto> mapToItemWithBookingDto(Iterable<Item> items) {
//        List<ItemWithBookingDto> dtos = new ArrayList<>();
//        for (Item item : items) {
//            dtos.add(mapToItemWithBookingDto(item));
//        }
//        return dtos;
//    }

}

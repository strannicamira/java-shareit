package ru.practicum.shareit.request;

import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;
import java.util.List;

public class ItemRequestWithItemDto {

    private Integer id;
    private String description;
//    private User requester;
    private LocalDate created;

    private List<ItemDto> items;
}

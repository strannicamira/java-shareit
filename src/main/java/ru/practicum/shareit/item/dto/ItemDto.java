package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Integer id;
//    @NotEmpty
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest itemRequest;

//    public ItemDto(String name, String description, Boolean available, ItemRequest itemRequest) {
//        this.name = name;
//        this.description = description;
//        this.available = available;
//        this.itemRequest = itemRequest;
//    }
}

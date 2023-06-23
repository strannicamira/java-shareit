package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
public class ItemDto {
    private Integer id;
    @NotEmpty
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;

    public ItemDto(String name, String description, Boolean available, ItemRequest request) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
    }
}

package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private Integer id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NotNull
    private Boolean available;
    private Integer owner;
    private ItemRequest itemRequest;

    public Item(Integer id, String name, String description, Boolean available, ItemRequest itemRequest) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.itemRequest = itemRequest;
    }
}

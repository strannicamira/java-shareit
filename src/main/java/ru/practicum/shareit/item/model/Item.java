package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

//@Data
//@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Item {
    @Id
    private Integer id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NotNull
    private Boolean available;
    private Integer owner;
    @Transient // TODO: tmp
    private ItemRequest itemRequest;

    public Item(Integer id, String name, String description, Boolean available, ItemRequest itemRequest) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.itemRequest = itemRequest;
    }
}

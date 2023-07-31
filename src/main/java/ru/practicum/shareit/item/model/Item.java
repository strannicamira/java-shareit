package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

//@Data
//@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items", schema = "public")//TODO: @Table is optional, but check name that should be the same
public class Item {
    @Id
    private Integer id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NotNull
    @Column(name = "is_available")
    private Boolean available;
    @Transient // TODO: tmp
    @Column(name = "owner_id")
    private User owner;
    @Transient // TODO: tmp
    @Column(name = "request_id")
    private ItemRequest itemRequest;

    public Item(Integer id, String name, String description, Boolean available, ItemRequest itemRequest) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.itemRequest = itemRequest;
    }
}

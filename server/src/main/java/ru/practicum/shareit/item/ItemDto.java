package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Integer id;
//    @NotEmpty
    private String name;
//    @NotEmpty
    private String description;
//    @NotNull
    private Boolean available;
    private Integer requestId;
}

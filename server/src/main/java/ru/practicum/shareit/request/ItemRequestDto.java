package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.item.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.util.Constants.TIME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private Integer id;
    private String description;
    @DateTimeFormat(pattern = TIME_PATTERN)
    private LocalDateTime created;
    private List<ItemDto> items;
}

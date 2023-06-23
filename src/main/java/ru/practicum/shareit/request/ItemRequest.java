package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Data
@Builder
public class ItemRequest {
    private Integer id;
    private String description;
    private User requestor;
    private LocalDate created;

}

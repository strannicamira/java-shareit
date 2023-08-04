package ru.practicum.shareit.comment;

import lombok.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentItemDto {
    private Integer id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}

package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class CommentDtoToCreate {
    @NotEmpty
    private String text;
}

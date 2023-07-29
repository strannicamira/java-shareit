package ru.practicum.shareit.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class User {
    private Integer id;
    @NotEmpty
    private String name;
    @NotEmpty
    @Email
    private String email;
}

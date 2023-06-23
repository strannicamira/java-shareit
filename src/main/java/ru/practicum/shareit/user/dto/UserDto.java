package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class UserDto {
    private Integer id;
    @NotEmpty
    private String name;
    @NotEmpty
    @Email
    private String email;

    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }
}

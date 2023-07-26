package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Integer id;
//    @NotEmpty
    private String name;
//    @NotEmpty
    @Email
    private String email;

//    public UserDto(String name, String email) {
//        this.name = name;
//        this.email = email;
//    }
}

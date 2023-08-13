package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    //    private Integer id;
//    @NotEmpty
    private String name;
//    @NotEmpty
//    @Email
    private String email;
}

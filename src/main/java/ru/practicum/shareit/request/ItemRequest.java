package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.time.LocalDate;

//@Data
//@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ItemRequest {
    @Id
    private Integer id;
    private String description;
    @Transient //TODO: tmp
    private User requester;
    private LocalDate created;
}

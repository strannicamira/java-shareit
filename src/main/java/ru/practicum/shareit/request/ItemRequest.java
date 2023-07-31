package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDate;

//@Data
//@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests", schema = "public")//TODO: @Table is optional, but check name that should be the same
public class ItemRequest {
    @Id
    private Integer id;
    private String description;
    @Transient //TODO: tmp
    @Column(name = "requester_id")
    private User requester;
    private LocalDate created;
}

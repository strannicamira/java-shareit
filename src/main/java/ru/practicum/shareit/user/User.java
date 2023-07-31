package ru.practicum.shareit.user;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

//@Data //TODO: Issue with @Data & @Entity due hashCode method: @Data should be deleted
//@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users", schema = "public")//TODO: @Table is optional, but check name that should be the same
public class User {
    @Id
//    @Column(name = "id") //TODO: Optional
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; //TODO: Long, String
    @NotEmpty
//    @Column(name = "name") //TODO: @Column is optional
    // TODO: PostgreSQL common to use first_name instead firstName column name for ex.
    // TODO: for ex. @Column(name = "first_name", nullable = false)
    // TODO: for ex.   @Column(name="STUDENT_NAME", length=128, nullable=false, unique=true)
    private String name;
    @NotEmpty
    @Email
//    @Column(name = "email")//TODO: Optional
    private String email;
}

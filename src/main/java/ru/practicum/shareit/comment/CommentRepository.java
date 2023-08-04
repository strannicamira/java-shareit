package ru.practicum.shareit.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.booking.Booking;

public interface CommentRepository extends JpaRepository<Comment, Integer>, QuerydslPredicateExecutor<Comment> {
}

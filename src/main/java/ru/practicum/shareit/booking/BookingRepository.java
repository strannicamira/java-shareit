package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface BookingRepository extends JpaRepository<Booking, Integer>, QuerydslPredicateExecutor<Booking> {
    void deleteByBookerIdAndId(Integer userId, Integer bookingId);
}

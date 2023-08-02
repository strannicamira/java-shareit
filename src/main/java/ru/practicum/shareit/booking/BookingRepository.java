package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer>, QuerydslPredicateExecutor<Booking> {
    List<Booking> findAllByBookerIdAndBookingState(Integer userId, BookingState bookingState);

    void deleteByBookerIdAndId(Integer userId, Integer bookingId);

    List<Booking> findAllByBookerId(Integer userId);
}

package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer>, QuerydslPredicateExecutor<Booking> {
    void deleteByBookerIdAndId(Integer userId, Integer bookingId);

    List<Booking> findAllByBookerId(Integer bookerId, Sort sortByStartDesc);
}

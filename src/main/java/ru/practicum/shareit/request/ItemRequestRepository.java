package ru.practicum.shareit.request;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer>, QuerydslPredicateExecutor<ItemRequest> {
    List<ItemRequest> findAllByRequesterId(Integer userId, Sort sortByRequestCreatedDesc);
}

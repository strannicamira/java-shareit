package ru.practicum.shareit.item;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer>, QuerydslPredicateExecutor<Item> {
    List<Item> findAllByOwnerId(Integer userId, Sort sortByIdDesc);

    Optional<Item> findByOwnerIdAndId(Integer userId, Integer itemId);

    void deleteByOwnerIdAndId(Integer userId, Integer itemId);

    List<Item> findAllByItemRequestId(Integer id);
}

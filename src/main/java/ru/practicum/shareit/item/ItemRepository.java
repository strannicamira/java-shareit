package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> { // TODO: https://github.com/praktikum-java/module4_spring_without_boot/blob/repositories/src/main/java/ru/practicum/item/ItemRepository.java
    List<Item> findByOwnerId(Integer userId);

    Optional<Item> findByOwnerIdAndId(Integer userId, Integer itemId);

    void deleteByOwnerIdAndId(Integer userId, Integer itemId);
}

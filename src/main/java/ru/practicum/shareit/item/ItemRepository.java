package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> { // TODO: https://github.com/praktikum-java/module4_spring_without_boot/blob/repositories/src/main/java/ru/practicum/item/ItemRepository.java
    List<Item> findByOwnerId(Integer userId);

    void deleteByOwnerIdAndId(Integer userId, Integer itemId);
}

package ru.practicum.shareit.item;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemDto getItem(Integer userId, Integer itemId) {
        log.info("Search item by item id {}", itemId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = repository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public List<ItemDto> getUserItems(Integer userId) {
        log.info("Search all items by user id {}", userId);
        List<Item> item = repository.findByOwnerId(userId);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getUserItems(Integer userId, String text) {
        log.info("Search all items by user id {} by matched text '{}'", userId, text);
        Iterable<Item> foundItems = new ArrayList<>();
        if (text != null && !text.isEmpty()) {
            BooleanExpression byAvailable = QItem.item.available.eq(true);
            BooleanExpression byTextInName = QItem.item.name.toLowerCase().contains(text.toLowerCase());
            BooleanExpression byTextInDescr = QItem.item.description.toLowerCase().contains(text.toLowerCase());

            foundItems = repository.findAll(byAvailable.and(byTextInName.or(byTextInDescr)));
        }
        return ItemMapper.mapToItemDto(foundItems);
    }


    @Override
    @Transactional
    public ItemDto addNewItem(Integer userId, Item item) {
        log.info("Create item by user id {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        item.setOwner(user);

        item = repository.save(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Integer userId, ItemDto itemDto, Integer itemId) {
        log.info("Update item by id {}", itemId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item itemById = repository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));

        if (!itemById.getOwner().getId().equals(user.getId())) {
            throw new IllegalStateException("Пользователь не владелец");
        }

        itemById.setName(itemDto.getName() == null || itemDto.getName().isBlank() ? itemById.getName() : itemDto.getName());
        itemById.setDescription(itemDto.getDescription() == null || itemDto.getDescription().isBlank() ? itemById.getDescription() : itemDto.getDescription());
        itemById.setAvailable(itemDto.getAvailable() == null ? itemById.getAvailable() : itemDto.getAvailable());
        itemById.setOwner(user);
        itemById.setItemRequest(itemDto.getItemRequest() == null ? itemById.getItemRequest() : itemDto.getItemRequest());
        Item item = repository.save(itemById); // TODO: save or saveAndFlash
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    @Transactional
    public void deleteItem(Integer userId, Integer itemId) {
        log.info("Delete item by user id {} by item id {}", userId, itemId);
        repository.deleteByOwnerIdAndId(userId, itemId);
    }
}

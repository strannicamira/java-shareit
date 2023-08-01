package ru.practicum.shareit.item;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserRepository userRepository;

    @Override
    public ItemDto getItem(Integer userId, Integer itemId) {
        log.info("Search item by item id {}", itemId);
        Item item = repository.findByOwnerIdAndId(userId, itemId).orElseThrow(() -> new IllegalStateException("Item not found"));
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
        List<Item> item = repository.findByOwnerId(userId);
        BooleanExpression byUserId = QItem.item.owner.id.eq(userId);
        BooleanExpression byTextInName = QItem.item.name.contains(text);
        BooleanExpression byTextInDescr = QItem.item.description.contains(text);
        Iterable<Item> foundItems = repository.findAll(byUserId.and(byTextInName.or(byTextInDescr)));
        return ItemMapper.mapToItemDto(foundItems);
    }


    @Transactional
    @Override
    public ItemDto addNewItem(Integer userId, ItemDto itemDto) {
        log.info("Create item by user id {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Item item = repository.save(ItemMapper.mapToItem(itemDto, user));
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(Integer userId, ItemDto itemDto, Integer itemId) {
        log.info("Update item by id {}", itemId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        itemDto.setId(itemId);//TODO: ?
        Item item = repository.save(ItemMapper.mapToItem(itemDto, user));
        return ItemMapper.mapToItemDto(item);
    }

    @Transactional
    @Override
    public void deleteItem(Integer userId, Integer itemId) {
        log.info("Delete item by user id {} by item id {}", userId, itemId);
        repository.deleteByOwnerIdAndId(userId, itemId);
    }
}

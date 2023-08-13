package ru.practicum.shareit.item;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.comment.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;//TODO
    private final BookingService bookingService;
    private final CommentRepository commentRepository;//TODO
    //    private final ItemRequestService requestService;
    private final ItemRequestRepository requestRepository;//TODO


    @Override
    @Transactional
    public ItemDto createItem(Integer userId, ItemDto itemDto) {
        log.info("Create item by user id {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = ItemMapper.mapToItem(itemDto, user);

        Integer requestId = itemDto.getRequestId();
        if (requestId != null) {
            ItemRequest itemRequest1 = requestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("Item request not found"));
            item.setItemRequest(itemRequest1);
        }

        item = itemRepository.save(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    @Transactional
    public CommentItemDto createItemComment(Integer userId, Integer itemId, Comment comment) {
        log.info("Create comment by user id {} for item id {}", userId, itemId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));

        List<BookingOutDto> pastBookingOutDtos = bookingService.getItemsBookingsByUser(itemId, userId, BookingState.PAST.getName());

        if (pastBookingOutDtos == null || pastBookingOutDtos.isEmpty()) {
            throw new IllegalStateException("User don't have passed bookings for this item to put comment");
        }

        if (comment.getText() == null || comment.getText().isBlank()) {
            throw new IllegalStateException("Empty comment");
        }

        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        Comment commentSaved = commentRepository.save(comment);
        return CommentMapper.mapToCommentItemDto(commentSaved);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Integer userId, ItemDtoForUpdate itemDto, Integer itemId) {
        log.info("Update item by id {}", itemId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getOwner().getId().equals(user.getId())) {
            throw new IllegalStateException("Пользователь не владелец");
        }

        item.setId(itemId);
        item.setName(itemDto.getName() == null || itemDto.getName().isBlank() ? item.getName() : itemDto.getName());
        item.setDescription(itemDto.getDescription() == null || itemDto.getDescription().isBlank() ? item.getDescription() : itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable() == null ? item.getAvailable() : itemDto.getAvailable());
        item.setOwner(user);

        Item itemSaved = itemRepository.save(item);
        return ItemMapper.mapToItemDto(itemSaved);
    }

    @Override
    public ItemDto getItem(Integer userId, Integer itemId) {
        log.info("Search item by item id {}", itemId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    @Transactional
    public ItemWithBookingDto getItemWithBooking(Integer userId, Integer itemId) {
        log.info("Search item by item id {}", itemId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        LastBooking lastBooking = null;
        NextBooking nextBooking = null;
        ItemWithBookingDto itemWithBookingDto = null;
        if (userId.equals(item.getOwner().getId())) {

            lastBooking = bookingService.getUserItemsLastPastBookings(userId, item);
            nextBooking = bookingService.getUserItemsFutureNextBookings(userId, item);

        }

        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        for (Comment comment : comments) {
            log.info("getItemWithBooking:comment.getCreated():" + comment.getCreated());
        }
        List<CommentItemDto> commentItemDtos = CommentMapper.mapToCommentItemDto(comments);
        for (CommentItemDto dto : commentItemDtos) {
            log.info("getItemWithBooking:dto.getCreated():" + dto.getCreated());
        }
        itemWithBookingDto = ItemWithBookingMapper.mapToItemWithBookingDto(item, lastBooking, nextBooking, commentItemDtos);

        return itemWithBookingDto;
    }

    @Override
    public List<ItemDto> getUserItems(Integer userId) {
        log.info("Search all items by user id {}", userId);
        List<Item> items = itemRepository.findAllByOwnerId(userId);

        return ItemMapper.mapToItemDto(items);
    }

    @Override
    public List<ItemWithBookingDto> getUserItemsWithBooking(Integer userId) {
        log.info("Search all items by user id {}", userId);
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<ItemWithBookingDto> dtos = new ArrayList<>();
        for (Item item : items) {
            ItemWithBookingDto itemWithBookingDto = getItemWithBooking(userId, item.getId());
            dtos.add(itemWithBookingDto);
        }
        return dtos;
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

            foundItems = itemRepository.findAll(byAvailable.and(byTextInName.or(byTextInDescr)));
        }
        return ItemMapper.mapToItemDto(foundItems);
    }

    @Override
    @Transactional
    public void deleteItem(Integer userId, Integer itemId) {
        log.info("Delete item by user id {} by item id {}", userId, itemId);
        itemRepository.deleteByOwnerIdAndId(userId, itemId);
    }
}

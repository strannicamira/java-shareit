package ru.practicum.shareit.item;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.comment.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
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
    private final BookingService bookingService;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;


    private static Integer runCount = 0;

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
    @Transactional
    public ItemWithBookingDto getItemWithBooking(Integer userId, Integer itemId) {
        runCount++;
        log.info("Search item by item id {}", itemId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));


        Item item = repository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\ngetItemWithBooking item ( {} ): {}\n", runCount, item);
        log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n\n");
        LastBooking lastBooking = null;
        NextBooking nextBooking = null;
        ItemWithBookingDto itemWithBookingDto = null;
        if (userId.equals(item.getOwner().getId())) {

            lastBooking = bookingService.getUserItemsLastPastBookings(userId, item);
            nextBooking = bookingService.getUserItemsFutureNextBookings(userId, item);

        }

        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        List<CommentItemDto> commentItemDtos = CommentMapper.mapToCommentItemDto(comments);
        itemWithBookingDto = ItemWithBookingMapper.mapToItemWithBookingDto(item, lastBooking, nextBooking, commentItemDtos);



        log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\ngetItemWithBooking itemWithBookingDto ( {} ): {}\n", runCount, itemWithBookingDto);
        log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n\n");


        return itemWithBookingDto;
    }

    @Override
    public List<ItemDto> getUserItems(Integer userId) {
        log.info("Search all items by user id {}", userId);
        List<Item> items = repository.findByOwnerId(userId);

        return ItemMapper.mapToItemDto(items);
    }

    @Override
    public List<ItemWithBookingDto> getUserItemsWithBooking(Integer userId) {
        log.info("Search all items by user id {}", userId);
        List<Item> items = repository.findByOwnerId(userId);
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
    public CommentItemDto addNewItemComment(Integer userId, Integer itemId, Comment comment) {
        log.info("Create comment by user id {} for item id", userId, itemId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = repository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));

        List<BookingOutDto> pastBookingOutDtos = bookingService.getItemsBookingsByUser(itemId, userId, BookingState.PAST.getName());

        if(pastBookingOutDtos==null || pastBookingOutDtos.isEmpty()){
            throw new NotOwnerException("User don't have passed bookings for this item to put comment");
        }

        comment.setItem(item);
        comment.setAuthor(user);
        Comment commentSaved = commentRepository.save(comment);
        return CommentMapper.mapToCommentItemDto(commentSaved);
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

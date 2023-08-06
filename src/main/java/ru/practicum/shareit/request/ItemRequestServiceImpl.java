package ru.practicum.shareit.request;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentItemDto;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.util.Constants.SORT_BY_REQUEST_CREATED_DESC;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto create(Integer userId, ItemRequest itemRequest) {
        log.info("Create item request by user id {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());

        itemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.mapToItemRequestDto(itemRequest,null);
    }

    @Override
    public List<ItemRequestDto> get(Integer userId) {
        log.info("Search item requests by requester id {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterId(userId, SORT_BY_REQUEST_CREATED_DESC);

        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for (ItemRequest request : itemRequests) {
            List<Item> items = itemRepository.findAllByItemRequestId(request.getId());
            List<ItemDto> dtos=  ItemMapper.mapToItemDto(items);
            itemRequestDtos.add(ItemRequestMapper.mapToItemRequestDto(request, dtos));
        }

        return itemRequestDtos;
    }

    @Override
    public ItemRequestDto get(Integer userId, Integer requestId) {
        log.info("Search item request by id {}", requestId);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Item not found"));
        List<Item> items = itemRepository.findAllByItemRequestId(itemRequest.getId());

        List<ItemDto> dtos = ItemMapper.mapToItemDto(items);
        return ItemRequestMapper.mapToItemRequestDto(itemRequest, dtos);
    }

    @Override
    public List<ItemDto> get(Integer userId, Integer from, Integer size) {
        return new ArrayList<ItemDto>();
    }
}

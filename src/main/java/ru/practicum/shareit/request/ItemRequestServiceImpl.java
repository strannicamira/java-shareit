package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        return ItemRequestMapper.mapToItemRequestDto(itemRequest, null);
    }

    @Override
    public List<ItemRequestDto> get(Integer userId) {
        log.info("Search item requests by requester id {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterId(userId, SORT_BY_REQUEST_CREATED_DESC);

        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();

        for (ItemRequest request : itemRequests) {
            List<Item> items = itemRepository.findAllByItemRequestId(request.getId());
            List<ItemDto> dtos = ItemMapper.mapToItemDto(items);
            itemRequestDtos.add(ItemRequestMapper.mapToItemRequestDto(request, dtos));
        }

        return itemRequestDtos;
    }

    @Override
    public ItemRequestDto get(Integer userId, Integer requestId) {
        log.info("Search item request by id {}", requestId);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Item request not found"));
        List<Item> items = itemRepository.findAllByItemRequestId(itemRequest.getId());

        List<ItemDto> dtos = ItemMapper.mapToItemDto(items);
        ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(itemRequest, dtos);
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> get(Integer userId, Integer from, Integer size) {
        log.info("Search item requests by request id {} page by page", userId);
        List<ItemRequest> itemRequests = new ArrayList<>();
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        if (from != null && size != null) {

            if (from < 0 || size < 0) {
                throw new IllegalStateException("Not correct page parameters");
            }
            PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, SORT_BY_REQUEST_CREATED_DESC);
            itemRequests = itemRequestRepository.findAll(page).getContent();

            for (ItemRequest request : itemRequests) {
                if (!userId.equals(request.getRequester().getId())) {
                    List<Item> items = itemRepository.findAllByItemRequestId(request.getId());
                    List<ItemDto> dtos = ItemMapper.mapToItemDto(items);
                    itemRequestDtos.add(ItemRequestMapper.mapToItemRequestDto(request, dtos));
                }
            }
        }
        return itemRequestDtos;
    }
}

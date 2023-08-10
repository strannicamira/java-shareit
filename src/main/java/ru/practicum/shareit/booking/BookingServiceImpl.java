package ru.practicum.shareit.booking;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.practicum.shareit.util.Constants.SORT_BY_ID_DESC;
import static ru.practicum.shareit.util.Constants.SORT_BY_START_DESC;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingOutDto createBooking(Integer userId, BookingDto bookingDto) {
        log.info("Create booking by booker id {}", userId);

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));


        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (userId.equals(item.getOwner().getId())) {
            throw new NotOwnerException("Booker is item owner");
        }

        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new NotAvailableException("Item is not available");
        }

        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new IllegalStateException("End is before start");
        }

        if (bookingDto.getEnd().isEqual(bookingDto.getStart())) {
            log.debug("End {} equals start {}", bookingDto.getEnd(), bookingDto.getStart());
            throw new IllegalStateException("End equals start");
        }

        bookingDto.setBookerId(userId);
        bookingDto.setStatus(BookingStatus.WAITING);

        Booking booking = bookingRepository.save(BookingMapper.mapToBooking(bookingDto, item, booker));

        return BookingOutMapper.mapToBookingOutDto(booking);
    }

    @Override
    @Transactional
    public BookingOutDto updateBooking(Integer userId, Integer bookingId, Boolean approved) {
        log.info("Update booking by id {} by accepting {}", bookingId, approved);

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotOwnerException("User is not owner");
        }

        BookingStatus bookingStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;

        if (approved && bookingStatus.equals(booking.getStatus())) {
            throw new IllegalStateException("Booking status was already APPROVED");
        }

        booking.setStatus(bookingStatus);

        Booking bookingSaved = bookingRepository.save(booking);
        return BookingOutMapper.mapToBookingOutDto(bookingSaved);
    }

    @Override
    @Transactional
    public BookingOutDto getBooking(Integer userId, Integer bookingId) {
        log.info("Search booking by booking id {}", bookingId);

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotOwnerException("User is not booker OR User is not item owner");
        }

        return BookingOutMapper.mapToBookingOutDto(booking);
    }

    private static Pageable getPage(Integer from, Integer size) {
        Sort sort = SORT_BY_START_DESC;
        Pageable page = null;
        if (from != null && size != null) {

            if (from < 0 || size < 0) {
                throw new IllegalStateException("Not correct page parameters");
            }
            page = PageRequest.of(from > 0 ? from / size : 0, size, sort);
        }
        return page;
    }

    private List<BookingOutDto> getBookingOutDtos(Integer userId, String state, BooleanExpression expression, Pageable page) {
        User booker = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        BookingState bookingState = validateBookingState(state);

        Iterable<Booking> bookings = getBookings(bookingState, expression, page);

        List<BookingOutDto> bookingOutDtos = BookingOutMapper.mapToBookingOutDto(bookings);

        return bookingOutDtos;
    }


    @Override
    @Transactional(readOnly = true)
    public List<BookingOutDto> getUserBookings(Integer userId, String state, Integer from, Integer size) {
        log.info("Search all bookings by user id {} by matched state '{}'", userId, state);
        BooleanExpression byBooker = QBooking.booking.booker.id.eq(userId);

        List<BookingOutDto> bookingOutDtos = new ArrayList<>();
        Pageable page = getPage(from, size);
        if (page != null) {
            bookingOutDtos = getBookingOutDtos(userId, state, byBooker, page);
        } else {
            bookingOutDtos = getBookingOutDtos(userId, state, byBooker, SORT_BY_START_DESC);

        }
        return bookingOutDtos;
    }


    @Override
    public List<BookingOutDto> getUserItemsBookings(Integer userId, String state, Integer from, Integer size) {
        log.info("Search all bookings by owner user id {}", userId);
        BooleanExpression byItem = QBooking.booking.item.owner.id.eq(userId);
        Pageable page = getPage(from, size);
        List<BookingOutDto> bookingOutDtos = new ArrayList<>();
        if (page != null) {
            bookingOutDtos = getBookingOutDtos(userId, state, byItem, page);
        } else {
            bookingOutDtos = getBookingOutDtos(userId, state, byItem, SORT_BY_START_DESC);

        }
        return bookingOutDtos;
    }

    @Override
    public List<BookingOutDto> getItemsBookingsByUser(Integer itemId, Integer userId, String state) {
        log.info("Search all bookings for item id {} by user id {}", itemId, userId);
        BooleanExpression byItem = QBooking.booking.item.id.eq(itemId);
        BooleanExpression byBooker = QBooking.booking.booker.id.eq(userId);
        BooleanExpression byBooking = byItem.and(byBooker);
        return getBookingOutDtos(userId, state, byBooking, SORT_BY_START_DESC);
    }

    @Override
    @Transactional
    public void deleteBooking(Integer userId, Integer bookingId) {
        log.info("Delete booking by user id {} by booking id {}", userId, bookingId);
        bookingRepository.deleteByBookerIdAndId(userId, bookingId);
    }


    private List<BookingOutDto> getBookingOutDtos(Integer userId, String state, BooleanExpression expression, Sort sort) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        BookingState bookingState = validateBookingState(state);

        Iterable<Booking> bookings = getBookings(bookingState, expression, sort);

        List<BookingOutDto> bookingOutDtos = BookingOutMapper.mapToBookingOutDto(bookings);

        return bookingOutDtos;
    }

    private static BookingState validateBookingState(String state) {
        BookingState bookingState = null;
        if (state != null) {
            bookingState = BookingState.forValues(state);
            if (bookingState == null) {
                throw new IllegalStateException("Unknown state: UNSUPPORTED_STATUS");
            }
        } else {
            bookingState = BookingState.ALL;
        }
        return bookingState;
    }


    private Iterable<Booking> getBookings(BookingState bookingState, BooleanExpression expression, Pageable page) {
        BooleanExpression byState = null;

        byState = getBooleanExpression(bookingState);

        Iterable<Booking> booking = bookingRepository.findAll(expression.and(byState), page);
        return booking;
    }

    private Iterable<Booking> getBookings(BookingState bookingState, BooleanExpression expression, Sort sort) {
        BooleanExpression byState = null;

        byState = getBooleanExpression(bookingState);

        Iterable<Booking> booking = bookingRepository.findAll(expression.and(byState), sort);
        return booking;
    }

    private static BooleanExpression getBooleanExpression(BookingState bookingState) {
        BooleanExpression byState = null;

        BooleanExpression byStart = null;
        BooleanExpression byEnd = null;
        BooleanExpression byStatus = null;

        switch (bookingState) {
            case CURRENT:
                log.info("CURRENT");
                byStart = QBooking.booking.start.before(LocalDateTime.now());
                byEnd = QBooking.booking.end.after(LocalDateTime.now());
                byState = byStart.and(byEnd);
                break;
            case PAST:
                log.info("PAST");
                byEnd = QBooking.booking.end.before(LocalDateTime.now());
                byState = byEnd;
                break;
            case FUTURE:
                log.info("FUTURE");
                byStart = QBooking.booking.start.after(LocalDateTime.now());
                byState = byStart;
                break;
            case WAITING:
                log.info("WAITING");
                byStatus = QBooking.booking.status.eq(BookingStatus.WAITING);
                byState = byStatus;
                break;
            case REJECTED:
                log.info("REJECTED");
                byStatus = QBooking.booking.status.eq(BookingStatus.REJECTED);
                byState = byStatus;
                break;
            case ALL:
            default:
                log.info("ALL");
                break;
        }
        return byState;
    }


    // to get Item with Booking -------------------------------------
    @Override
    public LastBooking getUserItemsLastPastBookings(Integer userId, Item item) {
        return getUserItemsLastBookings(getUserItemsPastBookings(userId, item));
    }

    @Override
    public NextBooking getUserItemsFutureNextBookings(Integer userId, Item item) {
        return getUserItemsNextBookings(getUserItemsFutureBookings(userId, item));
    }


    private List<BookingOutDto> getUserItemsFutureBookings(Integer userId, Item item) {

        List<BookingOutDto> bookingOutDtos = getBookingOutDtos(userId, item, BookingState.FUTURE.getName());
        log.info("getUserItemsFutureBookings");
        log.info("bookingLog:");
        for (BookingOutDto b : bookingOutDtos) {
            log.info("bookingLog:" + b.toString());
        }
        return bookingOutDtos;
    }

    private List<BookingOutDto> getUserItemsPastBookings(Integer userId, Item item) {
        List<BookingOutDto> curBookingOutDtos = getBookingOutDtos(userId, item, BookingState.CURRENT.getName());
        List<BookingOutDto> pastBookingOutDtos = getBookingOutDtos(userId, item, BookingState.PAST.getName());
        log.info("getUserItemsPastBookings");
        log.info("bookingLog:");
        for (BookingOutDto b : pastBookingOutDtos) {
            log.info("bookingLog:" + b.toString());
        }
        Collections.reverse(pastBookingOutDtos);
        log.info("getUserItemsPastBookings: after reverse");

        log.info("bookingLog:");
        for (BookingOutDto b : pastBookingOutDtos) {
            log.info("bookingLog:" + b.toString());
        }
        boolean b = pastBookingOutDtos.addAll(curBookingOutDtos);
        log.info("getUserItemsPastBookings: after add cur");

        log.info("bookingLog:");
        for (BookingOutDto bo : pastBookingOutDtos) {
            log.info("bookingLog:" + bo.toString());
        }
        return pastBookingOutDtos;
    }


    private List<BookingOutDto> getBookingOutDtos(Integer userId, Item item, String state) {
        log.info("Search all bookings for item {} in state {}", item, state);
        BooleanExpression byItem = QBooking.booking.item.id.eq(item.getId());

        BooleanExpression byStatus =
                QBooking.booking.status.in(BookingStatus.APPROVED, BookingStatus.WAITING);

        return getBookingOutDtos(userId, state, byItem.and(byStatus), SORT_BY_ID_DESC);
    }


    private LastBooking getUserItemsLastBookings(List<BookingOutDto> bookingOutDtos) {
        log.info("Search last booking");
        LastBooking lastBooking = null;
        if (bookingOutDtos != null && !bookingOutDtos.isEmpty()) {
            BookingOutDto bookingOutDto = bookingOutDtos.get(bookingOutDtos.size() - 1);
            lastBooking = new LastBooking(bookingOutDto.getId(), bookingOutDto.getBooker().getId());
        }
        return lastBooking;
    }

    private NextBooking getUserItemsNextBookings(List<BookingOutDto> bookingOutDtos) {
        log.info("Search next booking");
        NextBooking nextBooking = null;
        if (bookingOutDtos != null && !bookingOutDtos.isEmpty()) {
            BookingOutDto bookingOutDto = bookingOutDtos.get(0);
            nextBooking = new NextBooking(bookingOutDto.getId(), bookingOutDto.getBooker().getId());
        }
        return nextBooking;
    }
    // ---------------------------------


}

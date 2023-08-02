package ru.practicum.shareit.booking;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto createBooking(Integer userId, BookingDto bookingDto) {
        log.info("Create booking by booker id {}", userId);

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new NotAvailableException("Item is not available");
        }

//        if(booking.getEnd().isBefore(LocalDate.now())){
//            throw new RuntimeException("Booking end is in past");
//        }

        bookingDto.setBookerId(userId);
        bookingDto.setBookingStatus(BookingStatus.WAITING);

        Booking booking = repository.save(BookingMapper.mapToBooking(bookingDto, item, booker));

        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto updateBooking(Integer userId, Integer bookingId, Boolean approved) {
        log.info("Update booking by id {} by accepting {}", bookingId, approved);

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!userId.equals(booking.getBooker().getId())) {
            throw new IllegalStateException("User is not booker");
        }

        if (BookingStatus.WAITING.equals(booking.getBookingStatus())) {
            throw new IllegalStateException("Booking status is WAITING");//TODO:?
        }

        BookingStatus bookingStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;

        if (approved && bookingStatus.equals(booking.getBookingStatus())) {
            throw new IllegalStateException("Booking status was already APPROVED");//TODO:?
        }

        booking.setBookingStatus(bookingStatus);

        Booking bookingSaved = repository.save(booking); // TODO: save or saveAndFlash
        return BookingMapper.mapToBookingDto(bookingSaved);
    }

    @Override
    @Transactional
    public BookingDto getBooking(Integer userId, Integer bookingId) {
        log.info("Search booking by booking id {}", bookingId);

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!userId.equals(booking.getBooker().getId())) {
            throw new IllegalStateException("User is not booker");
        }

        if (!userId.equals(booking.getItem().getOwner())) {
            throw new IllegalStateException("User is not item owner");
        }

        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getUserBookings(Integer userId, String state) {
        log.info("Search all bookings by user id {} by matched state '{}'", userId, state);

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        BookingState bookingState = BookingState.forValues(state);

        if (bookingState == null) {
            throw new IllegalStateException("No such state");
        }

        BooleanExpression byBooker = QBooking.booking.booker.id.eq(userId);
        Iterable<Booking> booking = getBookings(bookingState, byBooker);
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserItemsBookings(Integer userId, String state) {
        log.info("Search all bookings by user id {}", userId);
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        BookingState bookingState = BookingState.forValues(state);

        if (bookingState == null) {
            throw new IllegalStateException("No such state");
        }

        BooleanExpression byItem = QBooking.booking.item.owner.id.eq(userId);
        Iterable<Booking> booking = getBookings(bookingState, byItem);
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    @Transactional
    public void deleteBooking(Integer userId, Integer bookingId) {
        log.info("Delete booking by user id {} by booking id {}", userId, bookingId);
        repository.deleteByBookerIdAndId(userId, bookingId);
    }

    private Iterable<Booking> getBookings(BookingState bookingState, BooleanExpression byBooking) {
        BooleanExpression byStart = null;
        BooleanExpression byEnd = null;
        BooleanExpression byStatus = null;

        switch (bookingState) {
            case CURRENT:
                log.info("CURRENT");
                byStart = QBooking.booking.start.before(LocalDate.now());
                byEnd = QBooking.booking.end.after(LocalDate.now());
                break;
            case PAST:
                log.info("PAST");
//                byStart = QBooking.booking.start.before(LocalDate.now());
                byEnd = QBooking.booking.end.before(LocalDate.now());
                break;
            case FUTURE:
                log.info("FUTURE");
                byStart = QBooking.booking.start.after(LocalDate.now());
//                byEnd = QBooking.booking.end.after(LocalDate.now());
                break;
            case WAITING:
                log.info("WAITING");
                byStatus = QBooking.booking.bookingStatus.eq(BookingStatus.WAITING);
                break;
            case REJECTED:
                log.info("REJECTED");
                byStatus = QBooking.booking.bookingStatus.eq(BookingStatus.REJECTED);
                break;
            case ALL:
            default:
                log.info("ALL");
                break;
        }

        Iterable<Booking> booking = repository.findAll(byBooking.and(byStart).and(byEnd).and(byStatus));
        return booking;
    }
}

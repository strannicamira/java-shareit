package ru.practicum.shareit.booking;

import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;

import static ru.practicum.shareit.util.Constants.SORT_BY_ID_DESC;

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

//        if(booking.getEnd().isBefore(LocalDate.now())){
//            throw new RuntimeException("Booking end is in past");
//        }

        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new IllegalStateException("End is before start");
        }

        if (bookingDto.getEnd().isEqual(bookingDto.getStart())) {
            log.debug("End {} equals start {}", bookingDto.getEnd(), bookingDto.getStart());
            throw new IllegalStateException("End equals start");
        }

        bookingDto.setBookerId(userId);
        bookingDto.setStatus(BookingStatus.WAITING);

        Booking booking = repository.save(BookingMapper.mapToBooking(bookingDto, item, booker));

        return BookingOutMapper.mapToBookingOutDto(booking);
    }

    @Override
    @Transactional
    public BookingOutDto updateBooking(Integer userId, Integer bookingId, Boolean approved) {
        log.info("Update booking by id {} by accepting {}", bookingId, approved);

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotOwnerException("User is not owner");
        }

//        if (!BookingStatus.WAITING.equals(booking.getStatus())) {
//            throw new IllegalStateException("Booking status is WAITING");
//        }

        BookingStatus bookingStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;

        if (approved && bookingStatus.equals(booking.getStatus())) {
            throw new IllegalStateException("Booking status was already APPROVED");//TODO:?
        }

        booking.setStatus(bookingStatus);

        Booking bookingSaved = repository.save(booking); // TODO: save or saveAndFlash
        return BookingOutMapper.mapToBookingOutDto(bookingSaved);
    }

    @Override
    @Transactional
    public BookingOutDto getBooking(Integer userId, Integer bookingId) {
        log.info("Search booking by booking id {}", bookingId);

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotOwnerException("User is not booker OR User is not item owner");
        }

        return BookingOutMapper.mapToBookingOutDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingOutDto> getUserBookings(Integer userId, String state) {
        log.info("Search all bookings by user id {} by matched state '{}'", userId, state);

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        BookingState bookingState = validateBookingState(state);

        BooleanExpression byBooker = QBooking.booking.booker.id.eq(userId);
        Iterable<Booking> bookings = getBookings(bookingState, byBooker);

        List<BookingOutDto> bookingOutDtos = BookingOutMapper.mapToBookingOutDto(bookings);
        if (bookingOutDtos == null || bookingOutDtos.isEmpty()) {
            throw new NotFoundException("Booking not found");
        }

        return bookingOutDtos;
    }

    @Override
    public List<BookingOutDto> getUserItemsBookings(Integer userId, String state) {
        log.info("Search all bookings by user id {}", userId);
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        BookingState bookingState = validateBookingState(state);

        BooleanExpression byItem = QBooking.booking.item.owner.id.eq(userId);
        Iterable<Booking> bookings = getBookings(bookingState, byItem);


        List<BookingOutDto> bookingOutDtos = BookingOutMapper.mapToBookingOutDto(bookings);
        if (bookingOutDtos == null || bookingOutDtos.isEmpty()) {
            throw new NotFoundException("Booking not found");
        }

        return bookingOutDtos;
    }

    @Override
    @Transactional
    public void deleteBooking(Integer userId, Integer bookingId) {
        log.info("Delete booking by user id {} by booking id {}", userId, bookingId);
        repository.deleteByBookerIdAndId(userId, bookingId);
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


    private Iterable<Booking> getBookings(BookingState bookingState, BooleanExpression byBooking) {
        BooleanExpression byStart = null;
        BooleanExpression byEnd = null;
        BooleanExpression byStatus = null;
        BooleanExpression bySwtich = null;

        switch (bookingState) {
            case CURRENT:
                log.info("CURRENT");
                byStart = QBooking.booking.start.before(LocalDateTime.now());
                byEnd = QBooking.booking.end.after(LocalDateTime.now());
                bySwtich = byStart.and(byEnd);
                break;
            case PAST:
                log.info("PAST");
//                byStart = QBooking.booking.start.before(LocalDate.now());
                byEnd = QBooking.booking.end.before(LocalDateTime.now());
                bySwtich = byEnd;
                break;
            case FUTURE:
                log.info("FUTURE");
                byStart = QBooking.booking.start.after(LocalDateTime.now());
//                byEnd = QBooking.booking.end.after(LocalDate.now());
                bySwtich = byStart;
                break;
            case WAITING:
                log.info("WAITING");
                byStatus = QBooking.booking.status.eq(BookingStatus.WAITING);
                bySwtich = byStatus;
                break;
            case REJECTED:
                log.info("REJECTED");
                byStatus = QBooking.booking.status.eq(BookingStatus.REJECTED);
                bySwtich = byStatus;
                break;
            case ALL:
            default:
                log.info("ALL");
                break;
        }

        Iterable<Booking> booking = repository.findAll(byBooking.and(bySwtich), SORT_BY_ID_DESC);
        return booking;
    }
}

package ru.practicum.shareit.booking;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemDtoForUpdate;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.util.Constants.*;

@Slf4j
@Transactional
@SpringBootTest(
        properties = "db.name=shareittest",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingServiceImplIntegrationTest {

    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;

    private static List<Integer> itemOwners;
    private static List<Integer> bookingOwners;
    private static List<Integer> items;
    private static List<Integer> bookings;
    private static List<Integer> notOwners;


    @BeforeAll
    static void setup() {
        itemOwners = new ArrayList<>();
        bookingOwners = new ArrayList<>();
        items = new ArrayList<>();
        bookings = new ArrayList<>();
        notOwners = new ArrayList<>();
    }

    @Order(1)
    @Test
    void createBooking() {
        //TODO: refactor
        UserDto userDto1 = makeUserDto("One Doe", "one@email.com");
        UserDto savedUserDto1 = userService.createUser(userDto1);
        Integer userId1 = savedUserDto1.getId();//=1
        itemOwners.add(userId1);

        UserDto userDto2 = makeUserDto("Two Doe", "two@email.com");
        UserDto savedUserDto2 = userService.createUser(userDto2);
        Integer userId2 = savedUserDto2.getId();//=2
        bookingOwners.add(userId2);

        ItemDto itemDto = makeAvailableItemDto("Onething", "One thing");
        ItemDto itemDtoCreated = itemService.createItem(userId1, itemDto);
        Integer itemId = itemDtoCreated.getId();//1
        items.add(itemId);

        LocalDateTime now = LocalDateTime.now();
        BookingDto bookingDto = makeBookingDto(now.plusDays(1), now.plusDays(2), itemId);

        log.info("Booking created by user id " + userId2 + " for item by id " + itemId + " owned by user by id " + userId1);
        BookingOutDto bookingOutDto = bookingService.createBooking(userId2, bookingDto);
        Integer bookingOutDtoId = bookingOutDto.getId();//=1
        bookings.add(bookingOutDtoId);

        Booking booking = bookingRepository.findById(bookingOutDtoId).orElseThrow(() -> new NotFoundException("Booking not found by id " + bookingOutDtoId));

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(bookingOutDto.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingOutDto.getEnd()));
        assertThat(booking.getItem().getId(), equalTo(bookingOutDto.getItem().getId()));
        assertThat(booking.getBooker().getId(), equalTo(bookingOutDto.getBooker().getId()));
        assertThat(booking.getStatus(), equalTo(BookingStatus.WAITING));
    }


    @Order(10)
    @Test
    void createBooking_viaBookingData() {
        BookingData bookingData = makeBookingData();
        BookingOutDto bookingOutDto = bookingData.bookingOutDto;
        Integer bookingId = bookingData.getBookingOutDto().getId();


        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found by id " + bookingId));

        assertThatBookingsEqual(bookingOutDto, booking);
    }


    @Order(11)
    @Test
    void createBooking_viaBookingData_whenBookerIsNotFound_thenThrowNotFoundException() {
        String itemOwnersName = "itemOwner" + itemOwners.size();
        String bookingOwnerName = "bookingOwner" + bookingOwners.size();
        String itemName = "item" + items.size();

        UserDto userDto1 = makeUserDto(itemOwnersName, itemOwnersName + "@email.com");
        UserDto itemOwnerUserDto = userService.createUser(userDto1);
        Integer itemOwnerId = itemOwnerUserDto.getId();//=3
        itemOwners.add(itemOwnerId);

        UserDto userDto2 = makeUserDto(bookingOwnerName, bookingOwnerName + "@email.com");
        UserDto bookingOwnerUserDto = userService.createUser(userDto2);
        Integer bookingOwnerId = bookingOwnerUserDto.getId();//=4
        bookingOwners.add(bookingOwnerId);

        ItemDto itemDto = makeAvailableItemDto(itemName, itemName + "...");
        ItemDto itemDtoCreated = itemService.createItem(itemOwnerId, itemDto);
        Integer itemId = itemDtoCreated.getId();//=2
        items.add(itemId);

        LocalDateTime now = LocalDateTime.now();
        BookingDto bookingDto = makeBookingDto(now.plusDays(1), now.plusDays(2), itemId);
        log.info("Booking created by user with id " + bookingOwnerId + " for item with id " + itemId + " owned by user with id " + itemOwnerId);

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(MAGIC_NUMBER, bookingDto));
    }

    @Order(12)
    @Test
    void createBooking_viaBookingData_whenItemIsNotFound_thenThrowNotFoundException() {
        String itemOwnersName = "itemOwner" + itemOwners.size();
        String bookingOwnerName = "bookingOwner" + bookingOwners.size();
        String itemName = "item" + items.size();

        UserDto userDto1 = makeUserDto(itemOwnersName, itemOwnersName + "@email.com");
        UserDto itemOwnerUserDto = userService.createUser(userDto1);
        Integer itemOwnerId = itemOwnerUserDto.getId();//=3
        itemOwners.add(itemOwnerId);

        UserDto userDto2 = makeUserDto(bookingOwnerName, bookingOwnerName + "@email.com");
        UserDto bookingOwnerUserDto = userService.createUser(userDto2);
        Integer bookingOwnerId = bookingOwnerUserDto.getId();//=4
        bookingOwners.add(bookingOwnerId);

        ItemDto itemDto = makeAvailableItemDto(itemName, itemName + "...");

        LocalDateTime now = LocalDateTime.now();
        BookingDto bookingDto = makeBookingDto(now.plusDays(1), now.plusDays(2), MAGIC_NUMBER);
        log.info("Booking created by user with id " + bookingOwnerId + " for item with id " + MAGIC_NUMBER + " owned by user with id " + itemOwnerId);

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(MAGIC_NUMBER, bookingDto));
    }

    @Order(13)
    @Test
    void createBooking_viaBookingData_whenBookerIsItemOwner_thenThrowNotOwnerException() {
        String itemOwnersName = "itemOwner" + itemOwners.size();
        String bookingOwnerName = "bookingOwner" + bookingOwners.size();
        String itemName = "item" + items.size();

        UserDto userDto1 = makeUserDto(itemOwnersName, itemOwnersName + "@email.com");
        UserDto itemOwnerUserDto = userService.createUser(userDto1);
        Integer itemOwnerId = itemOwnerUserDto.getId();//=3
        itemOwners.add(itemOwnerId);

        UserDto userDto2 = makeUserDto(bookingOwnerName, bookingOwnerName + "@email.com");
        UserDto bookingOwnerUserDto = userService.createUser(userDto2);
        Integer bookingOwnerId = bookingOwnerUserDto.getId();//=4
        bookingOwners.add(bookingOwnerId);

        ItemDto itemDto = makeAvailableItemDto(itemName, itemName + "...");
        ItemDto itemDtoCreated = itemService.createItem(itemOwnerId, itemDto);
        Integer itemId = itemDtoCreated.getId();//=2
        items.add(itemId);

        LocalDateTime now = LocalDateTime.now();
        BookingDto bookingDto = makeBookingDto(now.plusDays(1), now.plusDays(2), itemId);
        log.info("Booking created by user with id " + bookingOwnerId + " for item with id " + itemId + " owned by user with id " + itemOwnerId);

        assertThrows(NotOwnerException.class, () -> bookingService.createBooking(itemOwnerId, bookingDto));
    }

    @Order(14)
    @Test
    void createBooking_viaBookingData_whenItemIsNotAvailable_thenThrowNotAvailableException() {
        String itemOwnersName = "itemOwner" + itemOwners.size();
        String bookingOwnerName = "bookingOwner" + bookingOwners.size();
        String itemName = "item" + items.size();

        UserDto userDto1 = makeUserDto(itemOwnersName, itemOwnersName + "@email.com");
        UserDto itemOwnerUserDto = userService.createUser(userDto1);
        Integer itemOwnerId = itemOwnerUserDto.getId();//=3
        itemOwners.add(itemOwnerId);

        UserDto userDto2 = makeUserDto(bookingOwnerName, bookingOwnerName + "@email.com");
        UserDto bookingOwnerUserDto = userService.createUser(userDto2);
        Integer bookingOwnerId = bookingOwnerUserDto.getId();//=4
        bookingOwners.add(bookingOwnerId);

        ItemDto itemDto = makeNotAvailableItemDto(itemName, itemName + "...");
        ItemDto itemDtoCreated = itemService.createItem(itemOwnerId, itemDto);
        Integer itemId = itemDtoCreated.getId();//=2
        items.add(itemId);

        LocalDateTime now = LocalDateTime.now();
        BookingDto bookingDto = makeBookingDto(now.plusDays(1), now.plusDays(2), itemId);
        log.info("Booking created by user with id " + bookingOwnerId + " for item with id " + itemId + " owned by user with id " + itemOwnerId);

        assertThrows(NotAvailableException.class, () -> bookingService.createBooking(bookingOwnerId, bookingDto));
    }

    @Order(15)
    @Test
    void createBooking_viaBookingData_whenEndIsBeforeStart_thenThrowIllegalStateException() {
        String itemOwnersName = "itemOwner" + itemOwners.size();
        String bookingOwnerName = "bookingOwner" + bookingOwners.size();
        String itemName = "item" + items.size();

        UserDto userDto1 = makeUserDto(itemOwnersName, itemOwnersName + "@email.com");
        UserDto itemOwnerUserDto = userService.createUser(userDto1);
        Integer itemOwnerId = itemOwnerUserDto.getId();//=3
        itemOwners.add(itemOwnerId);

        UserDto userDto2 = makeUserDto(bookingOwnerName, bookingOwnerName + "@email.com");
        UserDto bookingOwnerUserDto = userService.createUser(userDto2);
        Integer bookingOwnerId = bookingOwnerUserDto.getId();//=4
        bookingOwners.add(bookingOwnerId);

        ItemDto itemDto = makeAvailableItemDto(itemName, itemName + "...");
        ItemDto itemDtoCreated = itemService.createItem(itemOwnerId, itemDto);
        Integer itemId = itemDtoCreated.getId();//=2
        items.add(itemId);

        LocalDateTime now = LocalDateTime.now();
        BookingDto bookingDto = makeBookingDto(now.plusDays(2), now.plusDays(1), itemId);
        log.info("Booking created by user with id " + bookingOwnerId + " for item with id " + itemId + " owned by user with id " + itemOwnerId);

        assertThrows(IllegalStateException.class, () -> bookingService.createBooking(bookingOwnerId, bookingDto));
    }

    @Order(15)
    @Test
    void createBooking_viaBookingData_whenEndEqualsStart_thenThrowIllegalStateException() {
        //TODO:
        String itemOwnersName = "itemOwner" + itemOwners.size();
        String bookingOwnerName = "bookingOwner" + bookingOwners.size();
        String itemName = "item" + items.size();

        UserDto userDto1 = makeUserDto(itemOwnersName, itemOwnersName + "@email.com");
        UserDto itemOwnerUserDto = userService.createUser(userDto1);
        Integer itemOwnerId = itemOwnerUserDto.getId();//=3
        itemOwners.add(itemOwnerId);

        UserDto userDto2 = makeUserDto(bookingOwnerName, bookingOwnerName + "@email.com");
        UserDto bookingOwnerUserDto = userService.createUser(userDto2);
        Integer bookingOwnerId = bookingOwnerUserDto.getId();//=4
        bookingOwners.add(bookingOwnerId);

        ItemDto itemDto = makeAvailableItemDto(itemName, itemName + "...");
        ItemDto itemDtoCreated = itemService.createItem(itemOwnerId, itemDto);
        Integer itemId = itemDtoCreated.getId();//=2
        items.add(itemId);

        LocalDateTime now = LocalDateTime.now();

        BookingDto bookingDto = makeBookingDto(now, now, itemId);
        log.info("Booking created by user with id " + bookingOwnerId + " for item with id " + itemId + " owned by user with id " + itemOwnerId);

        assertThrows(IllegalStateException.class, () -> bookingService.createBooking(bookingOwnerId, bookingDto));
    }


    @Order(20)
    @Test
    void updateBookingByApproving() {
        //TODO: refactor

        UserDto userDto1 = makeUserDto("Three Doe", "three@email.com");
        UserDto savedUserDto1 = userService.createUser(userDto1);
        Integer userId1 = savedUserDto1.getId();//=3
        itemOwners.add(userId1);

        UserDto userDto2 = makeUserDto("Four Doe", "four@email.com");
        UserDto savedUserDto2 = userService.createUser(userDto2);
        Integer userId2 = savedUserDto2.getId();//=4
        bookingOwners.add(userId2);

        ItemDto itemDto = makeAvailableItemDto("Twothing", "Two thing");
        ItemDto itemDtoCreated = itemService.createItem(userId1, itemDto);
        Integer itemId = itemDtoCreated.getId();//=2
        items.add(itemId);

        LocalDateTime now = LocalDateTime.now();
        BookingDto bookingDto = makeBookingDto(now.plusDays(1), now.plusDays(2), itemId);

        log.info("Booking created by user id " + userId2 + " for item by " + itemId + " owned by user by id " + userId1);
        BookingOutDto createdBookingOutDto = bookingService.createBooking(userId2, bookingDto);
        Integer createdBookingOutDtoId = createdBookingOutDto.getId();//=2
        bookings.add(createdBookingOutDtoId);

        log.info("Booking with id " + createdBookingOutDtoId + " updated by user id " + userId1 + " for item by " + itemId + " owned by user by id " + userId1);
        BookingOutDto updatedBookingOutDto = bookingService.updateBooking(userId1, createdBookingOutDtoId, Boolean.TRUE);
        Integer updatedBookingOutDtoId = updatedBookingOutDto.getId();

        Booking booking = bookingRepository.findById(updatedBookingOutDtoId).orElseThrow(() -> new NotFoundException("Booking not found by id " + updatedBookingOutDtoId));

        assertThat(booking.getId(), equalTo(updatedBookingOutDto.getId()));
        assertThat(booking.getStart(), equalTo(updatedBookingOutDto.getStart()));
        assertThat(booking.getEnd(), equalTo(updatedBookingOutDto.getEnd()));
        assertThat(booking.getItem().getId(), equalTo(updatedBookingOutDto.getItem().getId()));
        assertThat(booking.getBooker().getId(), equalTo(updatedBookingOutDto.getBooker().getId()));
        assertThat(booking.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Order(21)
    @Test
    void updateBookingByRejecting_viaBookingData() {

        BookingData bookingData = makeBookingData();
        Integer bookingId = bookingData.getBookingOutDto().getId();
        Integer itemOwnerId = bookingData.getItemOwner().getId();

        BookingOutDto updatedBookingOutDto = bookingService.updateBooking(itemOwnerId, bookingId, Boolean.FALSE);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found by id " + bookingId));

        assertThat(booking.getId(), equalTo(updatedBookingOutDto.getId()));
        assertThat(booking.getStart(), equalTo(updatedBookingOutDto.getStart()));
        assertThat(booking.getEnd(), equalTo(updatedBookingOutDto.getEnd()));
        assertThat(booking.getItem().getId(), equalTo(updatedBookingOutDto.getItem().getId()));
        assertThat(booking.getBooker().getId(), equalTo(updatedBookingOutDto.getBooker().getId()));
        assertThat(booking.getStatus(), equalTo(BookingStatus.REJECTED));
    }


    @Order(22)
    @Test
    void updateBooking_viaBookingData_whenBookerIsNotFound_thenThrowNotFoundException() {
        BookingData bookingData = makeBookingData();
        Integer bookingId = bookingData.getBookingOutDto().getId();
        Integer itemOwnerId = bookingData.getItemOwner().getId();


        assertThrows(NotFoundException.class, () -> bookingService.updateBooking(MAGIC_NUMBER, bookingId, Boolean.FALSE));

    }

    @Order(23)
    @Test
    void updateBooking_viaBookingData_whenItemIsNotFound_thenThrowNotFoundException() {
        BookingData bookingData = makeBookingData();
        Integer bookingId = bookingData.getBookingOutDto().getId();
        Integer itemOwnerId = bookingData.getItemOwner().getId();


        assertThrows(NotFoundException.class, () -> bookingService.updateBooking(itemOwnerId, MAGIC_NUMBER, Boolean.FALSE));

    }

    @Order(24)
    @Test
    void updateBooking_viaBookingData_whenBookerIsNotItemOwner_thenThrowNotOwnerException() {
        BookingData bookingData = makeBookingData();
        Integer bookingId = bookingData.getBookingOutDto().getId();
        Integer bookerId = bookingData.bookingOwner.getId();
        Integer itemOwnerId = bookingData.getItemOwner().getId();


        assertThrows(NotOwnerException.class, () -> bookingService.updateBooking(bookerId, bookingId, Boolean.FALSE));

    }

    @Order(25)
    @Test
    void updateBooking_viaBookingData_whenBookingIsAlreadyApproved_thenThrowIllegalStateException() {
        BookingData bookingData = makeBookingData();
        Integer bookingId = bookingData.getBookingOutDto().getId();
        Integer bookerId = bookingData.bookingOwner.getId();
        Integer itemOwnerId = bookingData.getItemOwner().getId();

        bookingService.updateBooking(itemOwnerId, bookingId, Boolean.TRUE);
        assertThrows(IllegalStateException.class, () -> bookingService.updateBooking(itemOwnerId, bookingId, Boolean.TRUE));
    }

    @Order(30)
    @Test
    void getBooking() {

        BookingData bookingData = makeBookingData();

        Integer bookingId = bookingData.getBookingOutDto().getId();
        Integer itemId = bookingData.getItem().getId();
        Integer bookerId = bookingData.getBookingOwner().getId();
        Integer itemOwnerId = bookingData.getItemOwner().getId();

        log.info("Get booking by id " + bookingId + " booked by user id " + bookerId +
                " for item by " + itemId + " owned by user by id " + itemOwnerId);
        BookingOutDto gotBookingOutDto = bookingService.getBooking(bookerId, bookingId);
        Integer updatedBookingOutDtoId = gotBookingOutDto.getId();

        Booking booking = bookingRepository.findById(updatedBookingOutDtoId).orElseThrow(() -> new NotFoundException("Booking not found by id " + bookingId));

        assertThatBookingsEqual(gotBookingOutDto, booking);
    }

    @Order(31)
    @Test
    void getBooking_whenUserIsNotFound_thenThrowNotFoundException() {
        BookingData bookingData = makeBookingData();

        Integer bookingId = bookingData.getBookingOutDto().getId();
        Integer itemId = bookingData.getItem().getId();
        Integer bookerId = bookingData.getBookingOwner().getId();
        Integer itemOwnerId = bookingData.getItemOwner().getId();

//        assertThrows(NotFoundException.class, () -> bookingService.getBooking(bookerId, bookingId));
        assertThrows(NotFoundException.class, () -> bookingService.getBooking(MAGIC_NUMBER, bookingId));

    }

    @Order(32)
    @Test
    void getBooking_whenBookingIsNotFound_thenThrowNotFoundException() {
        BookingData bookingData = makeBookingData();

        Integer bookingId = bookingData.getBookingOutDto().getId();
        Integer itemId = bookingData.getItem().getId();
        Integer bookerId = bookingData.getBookingOwner().getId();
        Integer itemOwnerId = bookingData.getItemOwner().getId();

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(bookerId, MAGIC_NUMBER));
    }

    @Order(33)
    @Test
    void getBooking_whenUserIsNotBooker_thenThrowNotOwnerException() {
        UserDto userDto = makeUserDto("NotOwner", "NotOwner@email.com");
        UserDto notOwnerUserDto = userService.createUser(userDto);
        Integer notOwnerId = notOwnerUserDto.getId();
        notOwners.add(notOwnerId);

        BookingData bookingData = makeBookingData();

        Integer bookingId = bookingData.getBookingOutDto().getId();
        Integer itemId = bookingData.getItem().getId();
        Integer bookerId = bookingData.getBookingOwner().getId();
        Integer itemOwnerId = bookingData.getItemOwner().getId();

        log.info("Get booking by id " + notOwnerId + " booked by user id " + bookerId +
                " for item by " + itemId + " owned by user by id " + itemOwnerId);

        assertThrows(NotOwnerException.class, () -> bookingService.getBooking(notOwnerId, bookingId));
    }

    @Order(40)
    @Test
    void getUserBookings_ALL() {

        BookingData bookingData = makeBookingData();
        Integer bookerId = bookingData.getBookingOwner().getId();

        List<BookingOutDto> gotBookingOutDto = bookingService.getUserBookings(bookerId, BookingState.ALL.getName(), null, null);

        for (BookingOutDto dto : gotBookingOutDto) {
            log.info("Get booking by id " + dto.getId() + " booked by user id " + dto.getBooker().getId() +
                    " for item by " + dto.getItem().getId());
        }

        List<Booking> bookings = bookingRepository.findAllByBookerId(bookerId, SORT_BY_START_DESC);

        for (Booking booking : bookings) {
            log.info("Get from repo booking by id " + booking.getId() + " booked by user id " + booking.getBooker().getId() +
                    " for item by " + booking.getItem().getId());
        }

        assertThat(bookings.size(), equalTo(gotBookingOutDto.size()));
        for (int i = 0; i < bookings.size(); i++) {
            assertThatBookingsEqual(gotBookingOutDto.get(i), bookings.get(i));
        }
    }

    @Order(41)
    @Test
    void getUserBookings_CURRENT() {

        BookingData bookingData = makeBookingData();
        Integer bookerId = bookingData.getBookingOwner().getId();

        List<BookingOutDto> gotBookingOutDto = bookingService.getUserBookings(bookerId, BookingState.CURRENT.getName(), null, null);

        for (BookingOutDto dto : gotBookingOutDto) {
            log.info("Get booking by id " + dto.getId() + " booked by user id " + dto.getBooker().getId() +
                    " for item by " + dto.getItem().getId());
        }
        BooleanExpression byBooker = QBooking.booking.booker.id.eq(bookerId);
        BooleanExpression expression = byBooker;
        LocalDateTime now = LocalDateTime.now();
        BooleanExpression byStart = QBooking.booking.start.before(now);
        BooleanExpression byEnd = QBooking.booking.end.after(now);
        BooleanExpression byState = byStart.and(byEnd);

        List<Booking> bookings = (List<Booking>)bookingRepository.findAll(expression.and(byState),  SORT_BY_START_DESC);

        for (Booking booking : bookings) {
            log.info("Get from repo booking by id " + booking.getId() + " booked by user id " + booking.getBooker().getId() +
                    " for item by " + booking.getItem().getId());
        }

        assertThat(bookings.size(), equalTo(gotBookingOutDto.size()));
        for (int i = 0; i < bookings.size(); i++) {
            assertThatBookingsEqual(gotBookingOutDto.get(i), bookings.get(i));
        }
    }

    @Order(42)
    @Test
    void getUserBookings_PAST() {

        BookingData bookingData = makeBookingData();
        Integer bookerId = bookingData.getBookingOwner().getId();

        List<BookingOutDto> gotBookingOutDto = bookingService.getUserBookings(bookerId, BookingState.PAST.getName(), null, null);

        for (BookingOutDto dto : gotBookingOutDto) {
            log.info("Get booking by id " + dto.getId() + " booked by user id " + dto.getBooker().getId() +
                    " for item by " + dto.getItem().getId());
        }
        BooleanExpression byBooker = QBooking.booking.booker.id.eq(bookerId);
        BooleanExpression expression = byBooker;
        LocalDateTime now = LocalDateTime.now();
        BooleanExpression byEnd = QBooking.booking.end.before(LocalDateTime.now());
        BooleanExpression byState = byEnd;

        List<Booking> bookings = (List<Booking>)bookingRepository.findAll(expression.and(byState),  SORT_BY_START_DESC);

        for (Booking booking : bookings) {
            log.info("Get from repo booking by id " + booking.getId() + " booked by user id " + booking.getBooker().getId() +
                    " for item by " + booking.getItem().getId());
        }

        assertThat(bookings.size(), equalTo(gotBookingOutDto.size()));
        for (int i = 0; i < bookings.size(); i++) {
            assertThatBookingsEqual(gotBookingOutDto.get(i), bookings.get(i));
        }
    }

    @Order(43)
    @Test
    void getUserBookings_FUTURE() {

        BookingData bookingData = makeBookingData();
        Integer bookerId = bookingData.getBookingOwner().getId();

        List<BookingOutDto> gotBookingOutDto = bookingService.getUserBookings(bookerId, BookingState.FUTURE.getName(), null, null);

        for (BookingOutDto dto : gotBookingOutDto) {
            log.info("Get booking by id " + dto.getId() + " booked by user id " + dto.getBooker().getId() +
                    " for item by " + dto.getItem().getId());
        }
        BooleanExpression byBooker = QBooking.booking.booker.id.eq(bookerId);
        BooleanExpression expression = byBooker;
        LocalDateTime now = LocalDateTime.now();
        BooleanExpression byStart = QBooking.booking.start.after(LocalDateTime.now());
        BooleanExpression byState = byStart;

        List<Booking> bookings = (List<Booking>)bookingRepository.findAll(expression.and(byState),  SORT_BY_START_DESC);

        for (Booking booking : bookings) {
            log.info("Get from repo booking by id " + booking.getId() + " booked by user id " + booking.getBooker().getId() +
                    " for item by " + booking.getItem().getId());
        }

        assertThat(bookings.size(), equalTo(gotBookingOutDto.size()));
        for (int i = 0; i < bookings.size(); i++) {
            assertThatBookingsEqual(gotBookingOutDto.get(i), bookings.get(i));
        }
    }



    @Order(44)
    @Test
    void getUserBookings_WAITING() {

        BookingData bookingData = makeBookingData();
        Integer bookerId = bookingData.getBookingOwner().getId();

        List<BookingOutDto> gotBookingOutDto = bookingService.getUserBookings(bookerId, BookingState.WAITING.getName(), null, null);

        for (BookingOutDto dto : gotBookingOutDto) {
            log.info("Get booking by id " + dto.getId() + " booked by user id " + dto.getBooker().getId() +
                    " for item by " + dto.getItem().getId());
        }
        BooleanExpression byBooker = QBooking.booking.booker.id.eq(bookerId);
        BooleanExpression expression = byBooker;
        LocalDateTime now = LocalDateTime.now();
        BooleanExpression byStatus = QBooking.booking.status.eq(BookingStatus.WAITING);

        BooleanExpression byState = byStatus;

        List<Booking> bookings = (List<Booking>)bookingRepository.findAll(expression.and(byState),  SORT_BY_START_DESC);

        for (Booking booking : bookings) {
            log.info("Get from repo booking by id " + booking.getId() + " booked by user id " + booking.getBooker().getId() +
                    " for item by " + booking.getItem().getId());
        }

        assertThat(bookings.size(), equalTo(gotBookingOutDto.size()));
        for (int i = 0; i < bookings.size(); i++) {
            assertThatBookingsEqual(gotBookingOutDto.get(i), bookings.get(i));
        }
    }


    @Order(45)
    @Test
    void getUserBookings_REJECTED() {

        BookingData bookingData = makeBookingData();
        Integer bookerId = bookingData.getBookingOwner().getId();

        List<BookingOutDto> gotBookingOutDto = bookingService.getUserBookings(bookerId, BookingState.REJECTED.getName(), null, null);

        for (BookingOutDto dto : gotBookingOutDto) {
            log.info("Get booking by id " + dto.getId() + " booked by user id " + dto.getBooker().getId() +
                    " for item by " + dto.getItem().getId());
        }
        BooleanExpression byBooker = QBooking.booking.booker.id.eq(bookerId);
        BooleanExpression expression = byBooker;
        LocalDateTime now = LocalDateTime.now();
        BooleanExpression byStatus = QBooking.booking.status.eq(BookingStatus.REJECTED);

        BooleanExpression byState = byStatus;

        List<Booking> bookings = (List<Booking>)bookingRepository.findAll(expression.and(byState),  SORT_BY_START_DESC);

        for (Booking booking : bookings) {
            log.info("Get from repo booking by id " + booking.getId() + " booked by user id " + booking.getBooker().getId() +
                    " for item by " + booking.getItem().getId());
        }

        assertThat(bookings.size(), equalTo(gotBookingOutDto.size()));
        for (int i = 0; i < bookings.size(); i++) {
            assertThatBookingsEqual(gotBookingOutDto.get(i), bookings.get(i));
        }
    }
    @Order(46)
    @Test
    void getUserBookings_whenStatusIsUNSUPPORTED_STATUS_thenThrowIllegalStateException() {
        BookingData bookingData = makeBookingData();
        Integer bookerId = bookingData.getBookingOwner().getId();

        assertThrows(IllegalStateException.class, () -> bookingService.getUserBookings(bookerId, "UNSUPPORTED_STATUS", -20, -20));

    }

    @Order(47)
    @Test
    void getUserBookings_viaBookingData_whenFromIsNegative_thenThrowIllegalStateException() {
        BookingData bookingData = makeBookingData();
        Integer bookerId = bookingData.getBookingOwner().getId();

        assertThrows(IllegalStateException.class, () -> bookingService.getUserBookings(bookerId, BookingState.ALL.getName(), -20, 20));
    }

    @Order(48)
    @Test
    void getUserBookings_viaBookingData_whenSizeIsNegative_thenThrowIllegalStateException() {
        BookingData bookingData = makeBookingData();
        Integer bookerId = bookingData.getBookingOwner().getId();

        assertThrows(IllegalStateException.class, () -> bookingService.getUserBookings(bookerId, BookingState.ALL.getName(), 0, -20));
    }

    @Order(49)
    @Test
    void getUserBookings_viaBookingData_whenFromAndSizeIsNotNull_thenReturnList() {

        Integer from = 0;
        Integer size = 20;
        BookingData bookingData = makeBookingData();
        Integer bookerId = bookingData.getBookingOwner().getId();

        List<BookingOutDto> gotBookingOutDto = bookingService.getUserBookings(bookerId, BookingState.ALL.getName(), from, size);

        for (BookingOutDto dto : gotBookingOutDto) {
            log.info("Get booking by id " + dto.getId() + " booked by user id " + dto.getBooker().getId() +
                    " for item by " + dto.getItem().getId());
        }

        Pageable page = getPage(from, size, SORT_BY_START_DESC);
        List<Booking> bookings = bookingRepository.findAllByBookerId(bookerId, page);

        for (Booking booking : bookings) {
            log.info("Get from repo booking by id " + booking.getId() + " booked by user id " + booking.getBooker().getId() +
                    " for item by " + booking.getItem().getId());
        }

        assertThat(bookings.size(), equalTo(gotBookingOutDto.size()));
        for (int i = 0; i < bookings.size(); i++) {
            assertThatBookingsEqual(gotBookingOutDto.get(i), bookings.get(i));
        }
    }


    @Order(50)
    @Test
    void getItemsBookings() {

        BookingData bookingData = makeBookingData();
        Integer bookerId = bookingData.getBookingOwner().getId();
        Integer itemOwnerId = bookingData.getItemOwner().getId();

        List<BookingOutDto> dtos = bookingService
                .getItemsBookings(itemOwnerId, BookingState.ALL.getName(), null, null);

        log.info("dtos.size()=" + dtos.size());
        for (BookingOutDto dto : dtos) {
            log.info("Get booking by id " + dto.getId() + " booked by user id " + dto.getBooker().getId() +
                    " for item by " + dto.getItem().getId());
        }

        BooleanExpression byItem = QBooking.booking.item.owner.id.eq(itemOwnerId);
        List<Booking> bookings = (List<Booking>) bookingRepository.findAll(byItem, SORT_BY_START_DESC);

        log.info("bookings.size()=" + bookings.size());
        for (Booking booking : bookings) {
            log.info("Get from repo booking by id " + booking.getId() + " booked by user id " + booking.getBooker().getId() +
                    " for item by " + booking.getItem().getId());
        }

        assertThat(bookings.size(), equalTo(dtos.size()));

        for (int i = 0; i < bookings.size(); i++) {
            assertThatBookingsEqual(dtos.get(i), bookings.get(i));
        }
    }

    @Order(51)
    @Test
    void getItemsBookings_viaBookingData_whenFromAndSizeIsNotNull_thenReturnList() {

        BookingData bookingData = makeBookingData();
        Integer bookerId = bookingData.getBookingOwner().getId();
        Integer itemOwnerId = bookingData.getItemOwner().getId();

        Integer from = 0;
        Integer size = 20;
        List<BookingOutDto> dtos = bookingService
                .getItemsBookings(itemOwnerId, BookingState.ALL.getName(), from, size);

        log.info("dtos.size()=" + dtos.size());
        for (BookingOutDto dto : dtos) {
            log.info("Get booking by id " + dto.getId() + " booked by user id " + dto.getBooker().getId() +
                    " for item by " + dto.getItem().getId());
        }

        Pageable page = getPage(from, size, SORT_BY_START_DESC);

        BooleanExpression byItem = QBooking.booking.item.owner.id.eq(itemOwnerId);
        List<Booking> bookings = bookingRepository.findAll(byItem, page).getContent();

        log.info("bookings.size()=" + bookings.size());
        for (Booking booking : bookings) {
            log.info("Get from repo booking by id " + booking.getId() + " booked by user id " + booking.getBooker().getId() +
                    " for item by " + booking.getItem().getId());
        }

        assertThat(bookings.size(), equalTo(dtos.size()));

        for (int i = 0; i < bookings.size(); i++) {
            assertThatBookingsEqual(dtos.get(i), bookings.get(i));
        }
    }

    @Order(52)
    @Test
    void getItemBookings_viaBookingData_whenFromIsNegative_thenThrowIllegalStateException() {
        Integer from = -20;
        Integer size = 20;

        BookingData bookingData = makeBookingData();
        Integer itemOwnerId = bookingData.getItemOwner().getId();

        assertThrows(IllegalStateException.class, () -> bookingService.getItemsBookings(itemOwnerId, BookingState.ALL.getName(), from, size));
    }

    @Order(53)
    @Test
    void getItemBookings_viaBookingData_whenSizeIsNegative_thenThrowIllegalStateException() {
        Integer from = 0;
        Integer size = -20;

        BookingData bookingData = makeBookingData();
        Integer itemOwnerId = bookingData.getItemOwner().getId();

        assertThrows(IllegalStateException.class, () -> bookingService.getItemsBookings(itemOwnerId, BookingState.ALL.getName(), from, size));
    }
    
    @Order(60)
    @Test
    void getItemsBookingsByUser() {

    }

    @Order(99)
    @Test
    void deleteBooking() {
        log.info("Bookings size is " + bookings.size());
        for (int i = 0; i < bookings.size(); i++) {
            log.info("Delete booking by id " + bookings.get(i) + "  by user id " + bookingOwners.get(i));
            bookingService.deleteBooking(bookingOwners.get(i), bookings.get(i));
        }
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class BookingData {
        private BookingOutDto bookingOutDto;
        private ItemDto item;
        private UserDto itemOwner;
        private UserDto bookingOwner;

    }

    private BookingData makeBookingData() {
        String itemOwnersName = "itemOwner" + itemOwners.size();
        String bookingOwnerName = "bookingOwner" + bookingOwners.size();
        String itemName = "item" + items.size();

        UserDto userDto1 = makeUserDto(itemOwnersName, itemOwnersName + "@email.com");
        UserDto itemOwnerUserDto1 = userService.createUser(userDto1);
        Integer itemOwnerId = itemOwnerUserDto1.getId();//=3

        UserDto userDto2 = makeUserDto(bookingOwnerName, bookingOwnerName + "@email.com");
        UserDto bookingOwnerUserDto2 = userService.createUser(userDto2);
        Integer bookingOwnerId = bookingOwnerUserDto2.getId();//=4

        ItemDto itemDto = makeAvailableItemDto(itemName, itemName + "...");
        ItemDto itemDtoCreated = itemService.createItem(itemOwnerId, itemDto);
        Integer itemId = itemDtoCreated.getId();//=2

        LocalDateTime now = LocalDateTime.now();
        BookingDto bookingDto = makeBookingDto(now.plusDays(1), now.plusDays(2), itemId);

        log.info("Booking created by user id " + bookingOwnerId + " for item by " + itemId + " owned by user by id " + itemOwnerId);
        BookingOutDto booking = bookingService.createBooking(bookingOwnerId, bookingDto);
        Integer bookingId = booking.getId();//=2

        addToLists(itemOwnerId, bookingOwnerId, itemId, bookingId);

        return new BookingData(booking, itemDtoCreated, itemOwnerUserDto1, bookingOwnerUserDto2);
    }

    private BookingData makeBookingDataStepByStep(Boolean available) {
        String itemOwnersName = "itemOwner" + itemOwners.size();
        String bookingOwnerName = "bookingOwner" + bookingOwners.size();
        String itemName = "item" + items.size();

        UserDto userDto1 = makeUserDto(itemOwnersName, itemOwnersName + "@email.com");
        UserDto itemOwnerUserDto = userService.createUser(userDto1);
        Integer itemOwnerId = itemOwnerUserDto.getId();//=3
        itemOwners.add(itemOwnerId);

        UserDto userDto2 = makeUserDto(bookingOwnerName, bookingOwnerName + "@email.com");
        UserDto bookingOwnerUserDto = userService.createUser(userDto2);
        Integer bookingOwnerId = bookingOwnerUserDto.getId();//=4
        bookingOwners.add(bookingOwnerId);

        ItemDto itemDto;
        if (available) {
            itemDto = makeAvailableItemDto(itemName, itemName + "...");
        } else {
            itemDto = makeNotAvailableItemDto(itemName, itemName + "...");
        }

        ItemDto itemDtoCreated = itemService.createItem(itemOwnerId, itemDto);
        Integer itemId = itemDtoCreated.getId();//=2
        items.add(itemId);

        LocalDateTime now = LocalDateTime.now();
        BookingDto bookingDto = makeBookingDto(now.plusDays(1), now.plusDays(2), itemId);

        log.info("Booking created by user with id " + bookingOwnerId + " for item with id " + itemId + " owned by user with id " + itemOwnerId);
        BookingOutDto booking = bookingService.createBooking(bookingOwnerId, bookingDto);
        Integer bookingId = booking.getId();//=2
        bookings.add(bookingId);

        BookingData bookingData = new BookingData(booking, itemDtoCreated, itemOwnerUserDto, bookingOwnerUserDto);
        return bookingData;
    }


    private BookingData makeBookingDataByUseId(Integer userId) {
        UserDto userDto1 = makeUserDto("Doe" + userId, "user" + userId + "@email.com");
        UserDto itemOwnerUserDto1 = userService.createUser(userDto1);
        Integer userId1 = itemOwnerUserDto1.getId();//=3

        ItemDto itemDto = makeAvailableItemDto("Twothing", "Two thing");
        ItemDto itemDtoCreated = itemService.createItem(userId1, itemDto);
        Integer itemId = itemDtoCreated.getId();//=2

        LocalDateTime now = LocalDateTime.now();
        BookingDto bookingDto = makeBookingDto(now.plusDays(1), now.plusDays(2), itemId);

        log.info("Booking created by user id " + userId + " for item by " + itemId + " owned by user by id " + userId1);
        BookingOutDto createdBookingOutDto = bookingService.createBooking(userId, bookingDto);
        Integer createdBookingOutDtoId = createdBookingOutDto.getId();//=2

        return new BookingData(createdBookingOutDto, itemDtoCreated, itemOwnerUserDto1, userService.getUser(userId));
    }

    private BookingData makeBookingDataByBookerId(Integer bookerId) {
        String itemOwnersName = "itemOwner" + itemOwners.size();
        String bookingOwnerName = "bookingOwner" + bookingOwners.size();
        String itemName = "item" + items.size();

        UserDto userDto1 = makeUserDto(itemOwnersName, itemOwnersName + "@email.com");
        UserDto itemOwnerUserDto1 = userService.createUser(userDto1);
        Integer userId1 = itemOwnerUserDto1.getId();//=3

        ItemDto itemDto = makeAvailableItemDto(itemName, itemName + "...");
        ItemDto itemDtoCreated = itemService.createItem(userId1, itemDto);
        Integer itemId = itemDtoCreated.getId();//=2

        LocalDateTime now = LocalDateTime.now();
        BookingDto bookingDto = makeBookingDto(now.plusDays(1), now.plusDays(2), itemId);

        log.info("Booking created by user id " + bookerId + " for item by " + itemId + " owned by user by id " + userId1);
        BookingOutDto booking = bookingService.createBooking(bookerId, bookingDto);
        Integer bookingId = booking.getId();//=2

        addToLists(userId1, bookerId, itemId, bookingId);

        return new BookingData(booking, itemDtoCreated, itemOwnerUserDto1, userService.getUser(bookerId));
    }


    private static void addToLists(Integer userId1, Integer userId2, Integer itemId, Integer bookingId) {
        //TODO: move into makeBookingData after each object creating
        itemOwners.add(userId1);
        bookingOwners.add(userId2);
        items.add(itemId);
        bookings.add(bookingId);
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

    private UserDto makeUserDtoByName(String name) {
        UserDto dto = new UserDto();
        dto.setName(name);
        return dto;
    }

    private ItemDto makeAvailableItemDto(String name, String description) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(Boolean.TRUE);
        return dto;
    }


    private ItemDto makeNotAvailableItemDto(String name, String description) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(Boolean.FALSE);
        return dto;
    }

    private ItemDtoForUpdate makeItemDtoForUpdate(String name, String description, Boolean available) {
        ItemDtoForUpdate dto = new ItemDtoForUpdate();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        return dto;
    }


    private BookingDto makeBookingDto(LocalDateTime start, LocalDateTime end, Integer itemId) {
        BookingDto dto = new BookingDto();
        dto.setStart(start);
        dto.setEnd(end);
        dto.setItemId(itemId);
        return dto;
    }


    private static Pageable getPage(Integer from, Integer size, Sort sort) {
        Pageable page = PageRequest.of(0, PAGE_SIZE, sort);
        if (from != null && size != null) {

            if (from < 0 || size <= 0) {
                throw new IllegalStateException("Not correct page parameters");
            }
            page = PageRequest.of(from > 0 ? from / size : 0, size, sort);
        }
        return page;
    }


    private static void assertThatBookingsEqual(BookingOutDto gotBookingOutDto, Booking booking) {
        assertAll(
                () -> assertThat(booking.getId(), equalTo(gotBookingOutDto.getId())),
                () -> assertThat(booking.getStart(), equalTo(gotBookingOutDto.getStart())),
                () -> assertThat(booking.getEnd(), equalTo(gotBookingOutDto.getEnd())),
                () -> assertThat(booking.getItem().getId(), equalTo(gotBookingOutDto.getItem().getId())),
                () -> assertThat(booking.getBooker().getId(), equalTo(gotBookingOutDto.getBooker().getId())),
                () -> assertThat(booking.getStatus(), equalTo(gotBookingOutDto.getStatus()))
        );
    }
}

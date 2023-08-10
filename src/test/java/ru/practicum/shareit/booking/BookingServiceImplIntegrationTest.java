package ru.practicum.shareit.booking;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
@Transactional
@SpringBootTest(
        properties = "db.name=shareittest",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingServiceImplIntegrationTest {

    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingService bookingService;

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    private static List<Integer> itemOwners;
    private static List<Integer> bookingOwners;
    private static List<Integer> items;
    private static List<Integer> bookings;


    @BeforeAll
    static void setup() {
        itemOwners = new ArrayList<>();
        bookingOwners = new ArrayList<>();
        items = new ArrayList<>();
        bookings = new ArrayList<>();
    }

    @Order(1)
    @Test
    void createBooking() {
        UserDto userDto1 = makeUserDto("One Doe", "one@email.com");
        UserDto savedUserDto1 = userService.createUser(userDto1);
        Integer userId1 = savedUserDto1.getId();//=1
        itemOwners.add(userId1);

        UserDto userDto2 = makeUserDto("Two Doe", "two@email.com");
        UserDto savedUserDto2 = userService.createUser(userDto2);
        Integer userId2 = savedUserDto2.getId();//=2
        bookingOwners.add(userId2);

        ItemDto itemDto = makeItemDto("Onething", "One thing");
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

    @Order(2)
    @Test
    void updateBookingByApproving() {
        UserDto userDto1 = makeUserDto("Three Doe", "three@email.com");
        UserDto savedUserDto1 = userService.createUser(userDto1);
        Integer userId1 = savedUserDto1.getId();//=3
        itemOwners.add(userId1);

        UserDto userDto2 = makeUserDto("Four Doe", "four@email.com");
        UserDto savedUserDto2 = userService.createUser(userDto2);
        Integer userId2 = savedUserDto2.getId();//=4
        bookingOwners.add(userId2);

        ItemDto itemDto = makeItemDto("Twothing", "Two thing");
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

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(updatedBookingOutDto.getStart()));
        assertThat(booking.getEnd(), equalTo(updatedBookingOutDto.getEnd()));
        assertThat(booking.getItem().getId(), equalTo(updatedBookingOutDto.getItem().getId()));
        assertThat(booking.getBooker().getId(), equalTo(updatedBookingOutDto.getBooker().getId()));
        assertThat(booking.getStatus(), equalTo(BookingStatus.APPROVED));
    }


    @Order(3)
    @Test
    void getBooking() {

        BookingData bookingData = makeBookingData();

        Integer bookingId = bookingData.getBookingOutDto().getId();
        Integer itemId = bookingData.getItem().getId();
        Integer bookerId = bookingData.getBookingOwner().getId();
        Integer itemOwnerId = bookingData.getItemOwner().getId();

        itemOwners.add(itemOwnerId);
        bookingOwners.add(bookerId);
        items.add(itemId);
        bookings.add(bookingId);

        log.info("Get booking by id " + bookingId + " booked by user id " + bookerId +
                " for item by " + itemId + " owned by user by id " + itemOwnerId);
        BookingOutDto gotBookingOutDto = bookingService.getBooking(bookerId, bookingId);
        Integer updatedBookingOutDtoId = gotBookingOutDto.getId();

        Booking booking = bookingRepository.findById(updatedBookingOutDtoId).orElseThrow(() -> new NotFoundException("Booking not found by id " + bookingId));

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(gotBookingOutDto.getStart()));
        assertThat(booking.getEnd(), equalTo(gotBookingOutDto.getEnd()));
        assertThat(booking.getItem().getId(), equalTo(gotBookingOutDto.getItem().getId()));
        assertThat(booking.getBooker().getId(), equalTo(gotBookingOutDto.getBooker().getId()));
        assertThat(booking.getStatus(), equalTo(gotBookingOutDto.getStatus()));
    }

    @Order(4)
    @Test
    void deleteBooking() {
        log.info("Bookings size is " + bookings.size());
        for (int i = 0; i < bookings.size(); i++) {
            log.info("Delete booking by id " + bookings.get(i) + "  by user id " + bookingOwners.get(i));
            bookingService.deleteBooking(bookingOwners.get(i), bookings.get(i));
        }
    }

    @Data
    private static class BookingData {
        public final BookingOutDto bookingOutDto;
        public final ItemDto item;
        public final UserDto itemOwner;
        public final UserDto bookingOwner;

    }

    private BookingData makeBookingData() {
        UserDto userDto1 = makeUserDto("Three Doe", "three@email.com");
        UserDto itemOwnerUserDto1 = userService.createUser(userDto1);
        Integer userId1 = itemOwnerUserDto1.getId();//=3

        UserDto userDto2 = makeUserDto("Four Doe", "four@email.com");
        UserDto bookingOwnerUserDto2 = userService.createUser(userDto2);
        Integer userId2 = bookingOwnerUserDto2.getId();//=4

        ItemDto itemDto = makeItemDto("Twothing", "Two thing");
        ItemDto itemDtoCreated = itemService.createItem(userId1, itemDto);
        Integer itemId = itemDtoCreated.getId();//=2

        LocalDateTime now = LocalDateTime.now();
        BookingDto bookingDto = makeBookingDto(now.plusDays(1), now.plusDays(2), itemId);

        log.info("Booking created by user id " + userId2 + " for item by " + itemId + " owned by user by id " + userId1);
        BookingOutDto createdBookingOutDto = bookingService.createBooking(userId2, bookingDto);
        Integer createdBookingOutDtoId = createdBookingOutDto.getId();//=2

        return new BookingData(createdBookingOutDto, itemDtoCreated, itemOwnerUserDto1, bookingOwnerUserDto2);
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

    private ItemDto makeItemDto(String name, String description) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(Boolean.TRUE);
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
}

package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
@Transactional
@SpringBootTest(
        properties = "db.name=shareittest",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplIntegrationTest {

    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingService bookingService;

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Test
    void createBooking() {
        UserDto userDto1 = makeUserDto("One Doe", "one@email.com");
        UserDto savedUserDto1 = userService.createUser(userDto1);
        Integer userId1 = savedUserDto1.getId();

        UserDto userDto2 = makeUserDto("Two Doe", "two@email.com");
        UserDto savedUserDto2 = userService.createUser(userDto2);
        Integer userId2 = savedUserDto2.getId();

        ItemDto itemDto = makeItemDto("Onething", "One thing");
        ItemDto itemDtoCreated = itemService.createItem(userId1, itemDto);
        Integer itemId = itemDtoCreated.getId();

        LocalDateTime now = LocalDateTime.now();
        BookingDto bookingDto = makeBookingDto(now.plusDays(1), now.plusDays(2), itemId);

        log.info("Booking created by user id " + userId2 + " for item by " + itemId + " owned by user by id " + userId1);
        BookingOutDto bookingOutDto = bookingService.createBooking(userId2, bookingDto);
        Integer bookingOutDtoId = bookingOutDto.getId();

        Booking booking = bookingRepository.findById(bookingOutDtoId).orElseThrow(() -> new NotFoundException("Booking not found by id " + bookingOutDtoId));

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(bookingOutDto.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingOutDto.getEnd()));
        assertThat(booking.getItem().getId(), equalTo(bookingOutDto.getItem().getId()));
        assertThat(booking.getBooker().getId(), equalTo(bookingOutDto.getBooker().getId()));
        assertThat(booking.getStatus(), equalTo(BookingStatus.WAITING));
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

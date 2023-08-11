package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
@SpringBootTest(
        properties = "db.name=shareittest",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingOutMapperUnitTest {

    @Test
    void mapToBookingOutDtoTest() {
        LocalDateTime now = LocalDateTime.now();

        User userItemOwner = new User(1, "John Doe", "john@email.com");
        User user2 = new User(2, "Jane Doe", "jane@email.com");

        ItemRequest itemRequest = new ItemRequest(1, "I need something", user2, now.minusDays(10));
        Item item = new Item(1, "Something", "Some thing", Boolean.TRUE, userItemOwner, itemRequest);

        LastBooking lastBooking = new LastBooking(1, 3);
        NextBooking nextBooking = new NextBooking(2, 4);

//        Comment comment = new Comment(1, "Item comment", item, user2, now.minusDays(1));
//        List<Comment> comments = Arrays.asList(comment);
//        List<CommentItemDto> commentItemDtos = CommentMapper.mapToCommentItemDto(comments);

//        ItemWithBookingDto itemWithBookingDto = ItemWithBookingMapper.mapToItemWithBookingDto(item, lastBooking, nextBooking, commentItemDtos);
        Booking booking = new Booking(1, now.minusDays(1), now.plusDays(1), item, user2, BookingStatus.WAITING);

        BookingOutDto bookingOutDto = BookingOutMapper.mapToBookingOutDto(booking);

        assertAll(
                () -> assertThat(bookingOutDto.getId(), equalTo(booking.getId())),
                () -> assertThat(bookingOutDto.getStart(), equalTo(booking.getStart())),
                () -> assertThat(bookingOutDto.getEnd(), equalTo(booking.getEnd())),
                () -> assertThat(bookingOutDto.getItem().getId(), equalTo(booking.getItem().getId())),
                () -> assertThat(bookingOutDto.getItem().getName(), equalTo(booking.getItem().getName())),
                () -> assertThat(bookingOutDto.getBooker().getId(), equalTo(booking.getBooker().getId())),
                () -> assertThat(bookingOutDto.getStatus(), equalTo(booking.getStatus()))

        );
    }


    @Test
    void mapToListofBookingOutDtoTest() {
        LocalDateTime now = LocalDateTime.now();

        User userItemOwner = new User(1, "John Doe", "john@email.com");
        User user2 = new User(2, "Jane Doe", "jane@email.com");

        ItemRequest itemRequest = new ItemRequest(1, "I need something", user2, now.minusDays(10));
        Item item = new Item(1, "Something", "Some thing", Boolean.TRUE, userItemOwner, itemRequest);

        LastBooking lastBooking = new LastBooking(1, 3);
        NextBooking nextBooking = new NextBooking(2, 4);

//        Comment comment = new Comment(1, "Item comment", item, user2, now.minusDays(1));
//        List<Comment> comments = Arrays.asList(comment);
//        List<CommentItemDto> commentItemDtos = CommentMapper.mapToCommentItemDto(comments);

//        ItemWithBookingDto itemWithBookingDto = ItemWithBookingMapper.mapToItemWithBookingDto(item, lastBooking, nextBooking, commentItemDtos);
        Booking booking = new Booking(1, now.minusDays(1), now.plusDays(1), item, user2, BookingStatus.WAITING);

        List<Booking> bookings = Arrays.asList(booking);
        List<BookingOutDto> bookingOutDtos = BookingOutMapper.mapToBookingOutDto(bookings);

        assertThat(bookingOutDtos.size(), equalTo(bookings.size()));
        for (int i = 0; i < bookingOutDtos.size(); i++) {
            int finalI = i;
            assertAll(
                    () -> assertThat(bookingOutDtos.get(finalI).getId(), equalTo(bookings.get(finalI).getId())),
                    () -> assertThat(bookingOutDtos.get(finalI).getStart(), equalTo(bookings.get(finalI).getStart())),
                    () -> assertThat(bookingOutDtos.get(finalI).getEnd(), equalTo(bookings.get(finalI).getEnd())),
                    () -> assertThat(bookingOutDtos.get(finalI).getItem().getId(), equalTo(bookings.get(finalI).getItem().getId())),
                    () -> assertThat(bookingOutDtos.get(finalI).getItem().getName(), equalTo(bookings.get(finalI).getItem().getName())),
                    () -> assertThat(bookingOutDtos.get(finalI).getBooker().getId(), equalTo(bookings.get(finalI).getBooker().getId())),
                    () -> assertThat(bookingOutDtos.get(finalI).getStatus(), equalTo(bookings.get(finalI).getStatus()))

            );
        }

    }
}

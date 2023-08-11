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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
@SpringBootTest(
        properties = "db.name=shareittest",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingMapperUnitTest {
    @Test
    void mapToBookingTest() {
        LocalDateTime now = LocalDateTime.now();

        User userItemOwner = new User(1, "John Doe", "john@email.com");
        User userBooker = new User(2, "Jane Doe", "jane@email.com");

        ItemRequest itemRequest = new ItemRequest(1, "I need something", userBooker, now.minusDays(10));
        Item item = new Item(1, "Something", "Some thing", Boolean.TRUE, userItemOwner, itemRequest);

        LastBooking lastBooking = new LastBooking(1, 3);
        NextBooking nextBooking = new NextBooking(2, 4);

//        Comment comment = new Comment(1, "Item comment", item, userBooker, now.minusDays(1));
//        List<Comment> comments = Arrays.asList(comment);
//        List<CommentItemDto> commentItemDtos = CommentMapper.mapToCommentItemDto(comments);

//        ItemWithBookingDto itemWithBookingDto = ItemWithBookingMapper.mapToItemWithBookingDto(item, lastBooking, nextBooking, commentItemDtos);
//        Booking booking = new Booking(1, now.minusDays(1), now.plusDays(1), item, userBooker, BookingStatus.WAITING);

//        BookingDto bookingDto = BookingMapper.mapToBookingDto(booking);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(now.minusDays(1));
        bookingDto.setEnd(now.plusDays(1));
        Booking booking = BookingMapper.mapToBooking( bookingDto,  item,  userBooker) ;

        assertAll(
                () -> assertThat(bookingDto.getId(), equalTo(booking.getId())),
                () -> assertThat(bookingDto.getStart(), equalTo(booking.getStart())),
                () -> assertThat(bookingDto.getEnd(), equalTo(booking.getEnd())),
                () -> assertThat(bookingDto.getItemId(), equalTo(booking.getItem().getId())),
                () -> assertThat(userBooker.getId(), equalTo(booking.getBooker().getId())),
                () -> assertThat(bookingDto.getStatus(), equalTo(booking.getStatus()))

        );
    }

    @Test
    void mapToBookingDtoTest() {
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

        BookingDto bookingDto = BookingMapper.mapToBookingDto(booking);

        assertAll(
                () -> assertThat(bookingDto.getId(), equalTo(booking.getId())),
                () -> assertThat(bookingDto.getStart(), equalTo(booking.getStart())),
                () -> assertThat(bookingDto.getEnd(), equalTo(booking.getEnd())),
                () -> assertThat(bookingDto.getItemId(), equalTo(booking.getItem().getId())),
                () -> assertThat(bookingDto.getBookerId(), equalTo(booking.getBooker().getId())),
                () -> assertThat(bookingDto.getStatus(), equalTo(booking.getStatus()))

        );
    }
}

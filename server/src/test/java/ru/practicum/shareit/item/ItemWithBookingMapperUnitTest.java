package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.LastBooking;
import ru.practicum.shareit.booking.NextBooking;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentItemDto;
import ru.practicum.shareit.comment.CommentMapper;
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
public class ItemWithBookingMapperUnitTest {
    @Test
    void mapToItemWithBookingDtoTest() {
        LocalDateTime now = LocalDateTime.now();

        User user1 = new User(1, "John Doe", "john@email.com");
        User user2 = new User(2, "Jane Doe", "jane@email.com");

        ItemRequest itemRequest = new ItemRequest(1, "I need something", user2, now.minusDays(10));
        Item item = new Item(1, "Something", "Some thing", Boolean.TRUE, user1, itemRequest);

        LastBooking lastBooking = new LastBooking(1, 3);
        NextBooking nextBooking = new NextBooking(2, 4);

        Comment comment = new Comment(1, "Item comment", item, user2, now.minusDays(1));
        List<Comment> comments = Arrays.asList(comment);
        List<CommentItemDto> commentItemDtos = CommentMapper.mapToCommentItemDto(comments);

        ItemWithBookingDto itemWithBookingDto = ItemWithBookingMapper.mapToItemWithBookingDto(item, lastBooking, nextBooking, commentItemDtos);

        assertAll(
                () -> assertThat(itemWithBookingDto.getId(), equalTo(item.getId())),
                () -> assertThat(itemWithBookingDto.getName(), equalTo(item.getName())),
                () -> assertThat(itemWithBookingDto.getDescription(), equalTo(item.getDescription())),
                () -> assertThat(itemWithBookingDto.getAvailable(), equalTo(item.getAvailable())),
                () -> assertThat(itemWithBookingDto.getLastBooking().getId(), equalTo(lastBooking.getId())),
                () -> assertThat(itemWithBookingDto.getLastBooking().getBookerId(), equalTo(lastBooking.getBookerId())),
                () -> assertThat(itemWithBookingDto.getNextBooking().getId(), equalTo(nextBooking.getId())),
                () -> assertThat(itemWithBookingDto.getNextBooking().getBookerId(), equalTo(nextBooking.getBookerId())),
                () -> assertThat(itemWithBookingDto.getItemRequest().getId(), equalTo(item.getItemRequest().getId())),
                () -> assertThat(itemWithBookingDto.getItemRequest().getDescription(), equalTo(item.getItemRequest().getDescription())),
                () -> assertThat(itemWithBookingDto.getItemRequest().getRequester().getId(), equalTo(item.getItemRequest().getRequester().getId())),
                () -> assertThat(itemWithBookingDto.getItemRequest().getRequester().getName(), equalTo(item.getItemRequest().getRequester().getName())),
                () -> assertThat(itemWithBookingDto.getItemRequest().getRequester().getEmail(), equalTo(item.getItemRequest().getRequester().getEmail())),
                () -> assertThat(itemWithBookingDto.getItemRequest().getCreated(), equalTo(item.getItemRequest().getCreated()))
        );

        assertThat(itemWithBookingDto.getComments().size(), equalTo(comments.size()));

        for (int i = 0; i < itemWithBookingDto.getComments().size(); i++) {
            int finalI = i;
            assertAll(
                    () -> assertThat(itemWithBookingDto.getComments().get(finalI).getId(), equalTo(comments.get(finalI).getId())),
                    () -> assertThat(itemWithBookingDto.getComments().get(finalI).getText(), equalTo(comments.get(finalI).getText())),
                    () -> assertThat(itemWithBookingDto.getComments().get(finalI).getAuthorName(), equalTo(comments.get(finalI).getAuthor().getName())),
                    () -> assertThat(itemWithBookingDto.getComments().get(finalI).getCreated(), equalTo(comments.get(finalI).getCreated()))
            );
        }
    }
}

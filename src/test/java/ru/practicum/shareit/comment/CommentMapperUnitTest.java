package ru.practicum.shareit.comment;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
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
public class CommentMapperUnitTest {
    //TODO: ?
    @Test
    void mapToCommentItemDto() {
        User user = new User(1, "John Doe", "some@email.com");
        LocalDateTime now = LocalDateTime.now();

        Comment comment = makeComment(1, "Text comment", user, now);
        CommentItemDto commentItemDto = CommentMapper.mapToCommentItemDto(comment);

        assertAll(
                () -> assertThat(commentItemDto.getId(), equalTo(comment.getId())),
                () -> assertThat(commentItemDto.getText(), equalTo(comment.getText())),
                () -> assertThat(commentItemDto.getAuthorName(), equalTo(comment.getAuthor().getName())),
                () -> assertThat(commentItemDto.getCreated(), equalTo(comment.getCreated()))
        );
    }

    @Test
    void mapToListCommentItemDto() {
        User user = new User(1, "John Doe", "some@email.com");
        LocalDateTime now = LocalDateTime.now();

        Comment comment = makeComment(1, "Text comment", user, now);
        List<Comment> comments = Arrays.asList(comment);
        List<CommentItemDto> commentItemDtos = CommentMapper.mapToCommentItemDto(comments);

        assertThat(commentItemDtos.size(), equalTo(comments.size()));

        for (int i = 0; i < commentItemDtos.size(); i++) {
            int finalI = i;
            assertAll(
                    () -> assertThat(commentItemDtos.get(finalI).getId(), equalTo(comments.get(finalI).getId())),
                    () -> assertThat(commentItemDtos.get(finalI).getText(), equalTo(comments.get(finalI).getText())),
                    () -> assertThat(commentItemDtos.get(finalI).getAuthorName(), equalTo(comments.get(finalI).getAuthor().getName())),
                    () -> assertThat(commentItemDtos.get(finalI).getCreated(), equalTo(comments.get(finalI).getCreated()))
            );
        }
    }


    private static Comment makeComment(Integer id, String text, User author, LocalDateTime created) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setText(text);
        comment.setAuthor(author);
        comment.setCreated(created);
        return comment;
    }


}

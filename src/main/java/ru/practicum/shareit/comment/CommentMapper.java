package ru.practicum.shareit.comment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    @Valid
    public static Comment mapToComment(CommentItemDto CommentItemDto, Item item, User user) {
        Comment comment = new Comment();
        comment.setId(CommentItemDto.getId()); 
        comment.setText(CommentItemDto.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(CommentItemDto.getCreated());
        return comment;
    }

    public static CommentItemDto mapToCommentItemDto(Comment comment) {
        return new CommentItemDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static List<CommentItemDto> mapToCommentItemDto(Iterable<Comment> comments) {
        List<CommentItemDto> dtos = new ArrayList<>();
        for (Comment comment : comments) {
            dtos.add(mapToCommentItemDto(comment));
        }
        return dtos;
    }

}

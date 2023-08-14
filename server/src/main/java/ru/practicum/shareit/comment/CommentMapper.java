package ru.practicum.shareit.comment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static CommentItemDto mapToCommentItemDto(Comment comment) {
        return new CommentItemDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    public static List<CommentItemDto> mapToCommentItemDto(Iterable<Comment> comments) {
        List<CommentItemDto> dtos = new ArrayList<>();
        for (Comment comment : comments) {
            dtos.add(mapToCommentItemDto(comment));
        }
        return dtos;
    }
}

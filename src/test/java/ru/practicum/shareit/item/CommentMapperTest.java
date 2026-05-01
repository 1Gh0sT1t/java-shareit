package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты маппера комментариев.
 */
class CommentMapperTest {

    @Test
    void shouldConvertCommentToDto() {
        User author = new User(1L, "Ivan", "ivan@mail.ru");

        Item item = new Item(
                10L,
                "Дрель",
                "Описание",
                true,
                author,
                null
        );

        LocalDateTime created = LocalDateTime.now();

        Comment comment = new Comment(
                100L,
                "Отличная вещь!",
                item,
                author,
                created
        );

        CommentDto dto = CommentMapper.toCommentDto(comment);

        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getText(), dto.getText());
        assertEquals(author.getName(), dto.getAuthorName());
        assertEquals(created, dto.getCreated());
    }

    @Test
    void shouldReturnNullWhenCommentIsNull() {
        assertNull(CommentMapper.toCommentDto(null));
    }
}
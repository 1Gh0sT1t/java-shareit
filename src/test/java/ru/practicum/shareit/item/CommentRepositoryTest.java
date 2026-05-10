package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тесты репозитория комментариев.
 */
@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindCommentsByItemId() {
        User owner = userRepository.save(new User(null, "Owner", "owner@mail.ru"));
        User author = userRepository.save(new User(null, "Author", "author@mail.ru"));
        Item item = itemRepository.save(new Item(null, "Дрель", "Хорошая дрель", true, owner, null));

        Comment comment = commentRepository.save(new Comment(
                null,
                "Отличная вещь",
                item,
                author,
                LocalDateTime.now()
        ));

        List<Comment> result = commentRepository.findByItemId(item.getId());

        assertEquals(1, result.size());
        assertEquals(comment.getId(), result.getFirst().getId());
        assertEquals(comment.getText(), result.getFirst().getText());
    }

    @Test
    void shouldFindCommentsByItemOwnerId() {
        User owner = userRepository.save(new User(null, "Owner", "owner2@mail.ru"));
        User author = userRepository.save(new User(null, "Author", "author2@mail.ru"));
        Item item = itemRepository.save(new Item(null, "Молоток", "Хороший молоток", true, owner, null));

        Comment comment = commentRepository.save(new Comment(
                null,
                "Очень удобно",
                item,
                author,
                LocalDateTime.now()
        ));

        List<Comment> result = commentRepository.findByItemOwnerId(owner.getId());

        assertEquals(1, result.size());
        assertEquals(comment.getId(), result.getFirst().getId());
    }
}
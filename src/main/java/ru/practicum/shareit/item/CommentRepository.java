package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

/**
 * Репозиторий комментариев.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByItemId(Long itemId);

    List<Comment> findByItemOwnerId(Long ownerId);

    List<Comment> findByItemIdIn(List<Long> itemIds);
}
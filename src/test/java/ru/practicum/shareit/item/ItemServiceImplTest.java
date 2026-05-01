package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тесты сервиса вещей.
 */
@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Owner", "owner@mail.ru");
        booker = new User(2L, "Booker", "booker@mail.ru");
        item = new Item(10L, "Дрель", "Хорошая дрель", true, owner, null);
    }

    @Test
    void shouldCreateItem() {
        Item newItem = new Item(null, "Дрель", "Хорошая дрель", true, null, null);

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.save(newItem)).thenReturn(item);

        Item result = itemService.create(newItem, owner.getId());

        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(owner, newItem.getOwner());

        verify(userRepository).findById(owner.getId());
        verify(itemRepository).save(newItem);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenOwnerNotFoundOnCreate() {
        Item newItem = new Item(null, "Дрель", "Хорошая дрель", true, null, null);

        when(userRepository.findById(owner.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(newItem, owner.getId()));
    }

    @Test
    void shouldThrowValidationExceptionWhenNameIsNullOnCreate() {
        Item newItem = new Item(null, null, "Описание", true, null, null);

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        assertThrows(ValidationException.class, () -> itemService.create(newItem, owner.getId()));
    }

    @Test
    void shouldThrowValidationExceptionWhenDescriptionIsBlankOnCreate() {
        Item newItem = new Item(null, "Дрель", " ", true, null, null);

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        assertThrows(ValidationException.class, () -> itemService.create(newItem, owner.getId()));
    }

    @Test
    void shouldThrowValidationExceptionWhenAvailableIsNullOnCreate() {
        Item newItem = new Item(null, "Дрель", "Описание", null, null, null);

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        assertThrows(ValidationException.class, () -> itemService.create(newItem, owner.getId()));
    }

    @Test
    void shouldUpdateOnlyName() {
        Item updateItem = new Item(10L, "Новая дрель", null, null, null, null);
        Item expectedItem = new Item(10L, "Новая дрель", "Хорошая дрель", true, owner, null);

        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(expectedItem);

        Item result = itemService.update(updateItem, owner.getId());

        assertEquals(expectedItem.getName(), result.getName());
        assertEquals(expectedItem.getDescription(), result.getDescription());
        assertEquals(expectedItem.getAvailable(), result.getAvailable());

        verify(itemRepository).findById(10L);
        verify(itemRepository).save(item);
    }

    @Test
    void shouldUpdateOnlyDescription() {
        Item updateItem = new Item(10L, null, "Новое описание", null, null, null);
        Item expectedItem = new Item(10L, "Дрель", "Новое описание", true, owner, null);

        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(expectedItem);

        Item result = itemService.update(updateItem, owner.getId());

        assertEquals(expectedItem.getName(), result.getName());
        assertEquals(expectedItem.getDescription(), result.getDescription());
        assertEquals(expectedItem.getAvailable(), result.getAvailable());
    }

    @Test
    void shouldUpdateOnlyAvailable() {
        Item updateItem = new Item(10L, null, null, false, null, null);
        Item expectedItem = new Item(10L, "Дрель", "Хорошая дрель", false, owner, null);

        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(expectedItem);

        Item result = itemService.update(updateItem, owner.getId());

        assertEquals(false, result.getAvailable());
    }

    @Test
    void shouldThrowValidationExceptionWhenItemIdIsNullOnUpdate() {
        Item updateItem = new Item(null, "Новое имя", null, null, null, null);

        assertThrows(ValidationException.class, () -> itemService.update(updateItem, owner.getId()));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemNotFoundOnUpdate() {
        Item updateItem = new Item(99L, "Новое имя", null, null, null, null);

        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.update(updateItem, owner.getId()));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenOtherUserUpdatesItem() {
        Item updateItem = new Item(10L, "Новое имя", null, null, null, null);

        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> itemService.update(updateItem, booker.getId()));
    }

    @Test
    void shouldThrowValidationExceptionWhenNameIsBlankOnUpdate() {
        Item updateItem = new Item(10L, " ", null, null, null, null);

        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> itemService.update(updateItem, owner.getId()));
    }

    @Test
    void shouldGetItemByIdForOwnerWithBookingsAndComments() {
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(
                org.mockito.Mockito.eq(10L),
                org.mockito.Mockito.any(),
                org.mockito.Mockito.any()
        )).thenReturn(Optional.empty());
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                org.mockito.Mockito.eq(10L),
                org.mockito.Mockito.any(),
                org.mockito.Mockito.any()
        )).thenReturn(Optional.empty());
        when(commentRepository.findByItemId(10L)).thenReturn(List.of());

        ItemResponseDto result = itemService.getById(10L, owner.getId());

        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
        assertEquals(0, result.getComments().size());
    }

    @Test
    void shouldGetItemByIdForNotOwnerWithoutBookings() {
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(10L)).thenReturn(List.of());

        ItemResponseDto result = itemService.getById(10L, booker.getId());

        assertEquals(item.getId(), result.getId());
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemNotFoundById() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getById(99L, owner.getId()));
    }

    @Test
    void shouldGetItemsByOwnerId() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(owner.getId())).thenReturn(List.of(item));
        when(bookingRepository.findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(
                org.mockito.Mockito.eq(10L),
                org.mockito.Mockito.any(),
                org.mockito.Mockito.any()
        )).thenReturn(Optional.empty());
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                org.mockito.Mockito.eq(10L),
                org.mockito.Mockito.any(),
                org.mockito.Mockito.any()
        )).thenReturn(Optional.empty());
        when(commentRepository.findByItemId(10L)).thenReturn(List.of());

        var result = itemService.getByOwnerId(owner.getId());

        assertEquals(1, result.size());
    }

    @Test
    void shouldReturnEmptyListWhenSearchTextIsBlank() {
        var result = itemService.search(" ");

        assertEquals(0, result.size());
    }

    @Test
    void shouldSearchItems() {
        when(itemRepository.search("дрель")).thenReturn(List.of(item));

        var result = itemService.search("дрель");

        assertEquals(1, result.size());
        verify(itemRepository).search("дрель");
    }

    @Test
    void shouldAddComment() {
        CommentDto input = new CommentDto(null, "Отличная вещь", null, null);
        LocalDateTime created = LocalDateTime.now();
        Comment savedComment = new Comment(1L, "Отличная вещь", item, booker, created);

        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.existsByItemIdAndBookerIdAndEndBefore(
                org.mockito.Mockito.eq(10L),
                org.mockito.Mockito.eq(booker.getId()),
                org.mockito.Mockito.any()
        )).thenReturn(true);
        when(commentRepository.save(org.mockito.Mockito.any(Comment.class))).thenReturn(savedComment);

        CommentDto result = itemService.addComment(10L, booker.getId(), input);

        assertEquals(savedComment.getId(), result.getId());
        assertEquals(savedComment.getText(), result.getText());
        assertEquals(booker.getName(), result.getAuthorName());
    }

    @Test
    void shouldThrowValidationExceptionWhenCommentTextIsBlank() {
        CommentDto input = new CommentDto(null, " ", null, null);

        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));

        assertThrows(ValidationException.class, () -> itemService.addComment(10L, booker.getId(), input));
    }

    @Test
    void shouldThrowValidationExceptionWhenUserHasNoCompletedBookingForComment() {
        CommentDto input = new CommentDto(null, "Отличная вещь", null, null);

        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.existsByItemIdAndBookerIdAndEndBefore(
                org.mockito.Mockito.eq(10L),
                org.mockito.Mockito.eq(booker.getId()),
                org.mockito.Mockito.any()
        )).thenReturn(false);

        assertThrows(ValidationException.class, () -> itemService.addComment(10L, booker.getId(), input));
    }
}
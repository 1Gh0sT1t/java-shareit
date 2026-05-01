package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Сервис для работы с вещами.
 */
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public Item create(Item item, Long userId) {
        User owner = getUserOrThrow(userId);
        validateItemForCreate(item);
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    @Override
    public Item update(Item item, Long userId) {
        Item savedItem = validateItemForUpdate(item, userId);

        if (item.getName() != null) {
            if (item.getName().isBlank()) {
                throw new ValidationException("Название вещи не должно быть пустым");
            }
            savedItem.setName(item.getName());
        }

        if (item.getDescription() != null) {
            if (item.getDescription().isBlank()) {
                throw new ValidationException("Описание вещи не должно быть пустым");
            }
            savedItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            savedItem.setAvailable(item.getAvailable());
        }

        return itemRepository.save(savedItem);
    }

    @Override
    public ItemResponseDto getById(Long itemId, Long userId) {
        Item item = getItemOrThrow(itemId);

        if (userId != null && item.getOwner().getId().equals(userId)) {
            return toResponseDtoWithBookings(item);
        }

        return ItemMapper.toItemResponseDto(item, null, null, getComments(item.getId()));
    }

    @Override
    public Collection<ItemResponseDto> getByOwnerId(Long ownerId) {
        getUserOrThrow(ownerId);

        List<Item> items = itemRepository.findByOwnerId(ownerId);

        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();

        LocalDateTime now = LocalDateTime.now();

        Map<Long, Booking> lastBookings = bookingRepository
                .findByItemIdInAndStatusAndEndBeforeOrderByEndDesc(itemIds, BookingStatus.APPROVED, now)
                .stream()
                .collect(Collectors.toMap(
                        booking -> booking.getItem().getId(),
                        booking -> booking,
                        (first, second) -> first
                ));

        Map<Long, Booking> nextBookings = bookingRepository
                .findByItemIdInAndStatusAndStartAfterOrderByStartAsc(itemIds, BookingStatus.APPROVED, now)
                .stream()
                .collect(Collectors.toMap(
                        booking -> booking.getItem().getId(),
                        booking -> booking,
                        (first, second) -> first
                ));

        Map<Long, List<CommentDto>> comments = commentRepository.findByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId(),
                        Collectors.mapping(CommentMapper::toCommentDto, Collectors.toList())
                ));

        return items.stream()
                .map(item -> ItemMapper.toItemResponseDto(
                        item,
                        ItemMapper.toBookingShortDto(lastBookings.get(item.getId())),
                        ItemMapper.toBookingShortDto(nextBookings.get(item.getId())),
                        comments.getOrDefault(item.getId(), Collections.emptyList())
                ))
                .toList();
    }

    @Override
    public Collection<Item> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.search(text);
    }

    @Override
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        Item item = getItemOrThrow(itemId);
        User author = getUserOrThrow(userId);

        validateComment(commentDto);

        boolean hasCompletedBooking = bookingRepository.existsByItemIdAndBookerIdAndEndBefore(
                itemId,
                userId,
                LocalDateTime.now()
        );

        if (!hasCompletedBooking) {
            throw new ValidationException("Оставить комментарий может только пользователь с завершённым бронированием");
        }

        Comment comment = CommentMapper.toComment(commentDto, item, author);

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private ItemResponseDto toResponseDtoWithBookings(Item item) {
        LocalDateTime now = LocalDateTime.now();

        Booking lastBooking = bookingRepository
                .findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(
                        item.getId(),
                        BookingStatus.APPROVED,
                        now
                )
                .orElse(null);

        Booking nextBooking = bookingRepository
                .findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                        item.getId(),
                        BookingStatus.APPROVED,
                        now
                )
                .orElse(null);

        BookingShortDto lastDto = ItemMapper.toBookingShortDto(lastBooking);
        BookingShortDto nextDto = ItemMapper.toBookingShortDto(nextBooking);

        return ItemMapper.toItemResponseDto(item, lastDto, nextDto, getComments(item.getId()));
    }

    private List<CommentDto> getComments(Long itemId) {
        return commentRepository.findByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .toList();
    }

    private void validateComment(CommentDto dto) {
        if (dto.getText() == null || dto.getText().isBlank()) {
            throw new ValidationException("Текст комментария не должен быть пустым");
        }
    }

    private void validateItemForCreate(Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("Название вещи не должно быть пустым");
        }

        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Описание вещи не должно быть пустым");
        }

        if (item.getAvailable() == null) {
            throw new ValidationException("Статус доступности вещи должен быть указан");
        }
    }

    private Item validateItemForUpdate(Item item, Long userId) {
        if (item.getId() == null) {
            throw new ValidationException("Идентификатор вещи не указан");
        }

        Item savedItem = getItemOrThrow(item.getId());

        if (!savedItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Редактировать вещь может только владелец");
        }

        return savedItem;
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден"));
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));
    }
}
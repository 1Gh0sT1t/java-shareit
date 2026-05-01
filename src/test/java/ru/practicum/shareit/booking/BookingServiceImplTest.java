package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тесты сервиса бронирований.
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Owner", "owner@mail.ru");
        booker = new User(2L, "Booker", "booker@mail.ru");
        item = new Item(10L, "Дрель", "Хорошая дрель", true, owner, null);

        start = LocalDateTime.now().plusDays(1);
        end = start.plusDays(2);

        booking = new Booking(
                100L,
                start,
                end,
                item,
                booker,
                BookingStatus.WAITING
        );
    }

    @Test
    void shouldCreateBooking() {
        BookingCreateDto createDto = new BookingCreateDto(item.getId(), start, end);

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(org.mockito.Mockito.any(Booking.class))).thenReturn(booking);

        Booking result = bookingService.create(createDto, booker.getId());

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(BookingStatus.WAITING, result.getStatus());
        assertEquals(booker.getId(), result.getBooker().getId());
        assertEquals(item.getId(), result.getItem().getId());

        verify(userRepository).findById(booker.getId());
        verify(itemRepository).findById(item.getId());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenBookerNotFoundOnCreate() {
        BookingCreateDto createDto = new BookingCreateDto(item.getId(), start, end);

        when(userRepository.findById(booker.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.create(createDto, booker.getId()));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemNotFoundOnCreate() {
        BookingCreateDto createDto = new BookingCreateDto(99L, start, end);

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.create(createDto, booker.getId()));
    }

    @Test
    void shouldThrowValidationExceptionWhenStartIsNullOnCreate() {
        BookingCreateDto createDto = new BookingCreateDto(item.getId(), null, end);

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(createDto, booker.getId()));
    }

    @Test
    void shouldThrowValidationExceptionWhenEndIsBeforeStartOnCreate() {
        BookingCreateDto createDto = new BookingCreateDto(item.getId(), start, start.minusHours(1));

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(createDto, booker.getId()));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenOwnerBooksOwnItem() {
        BookingCreateDto createDto = new BookingCreateDto(item.getId(), start, end);

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> bookingService.create(createDto, owner.getId()));
    }

    @Test
    void shouldThrowValidationExceptionWhenItemIsUnavailable() {
        Item unavailableItem = new Item(10L, "Дрель", "Описание", false, owner, null);
        BookingCreateDto createDto = new BookingCreateDto(unavailableItem.getId(), start, end);

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(unavailableItem.getId())).thenReturn(Optional.of(unavailableItem));

        assertThrows(ValidationException.class, () -> bookingService.create(createDto, booker.getId()));
    }

    @Test
    void shouldApproveBooking() {
        Booking approvedBooking = new Booking(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                BookingStatus.APPROVED
        );

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(approvedBooking);

        Booking result = bookingService.approve(booking.getId(), owner.getId(), true);

        assertEquals(BookingStatus.APPROVED, result.getStatus());

        verify(bookingRepository).findById(booking.getId());
        verify(bookingRepository).save(booking);
    }

    @Test
    void shouldRejectBooking() {
        Booking rejectedBooking = new Booking(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                BookingStatus.REJECTED
        );

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(rejectedBooking);

        Booking result = bookingService.approve(booking.getId(), owner.getId(), false);

        assertEquals(BookingStatus.REJECTED, result.getStatus());
    }

    @Test
    void shouldThrowValidationExceptionWhenApprovedIsNull() {
        assertThrows(ValidationException.class, () -> bookingService.approve(booking.getId(), owner.getId(), null));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenBookingNotFoundOnApprove() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.approve(99L, owner.getId(), true));
    }

    @Test
    void shouldThrowForbiddenExceptionWhenWrongUserApprovesBooking() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenException.class, () -> bookingService.approve(booking.getId(), booker.getId(), true));
    }

    @Test
    void shouldGetBookingByBooker() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        Booking result = bookingService.getById(booking.getId(), booker.getId());

        assertEquals(booking.getId(), result.getId());
        assertEquals(booker.getId(), result.getBooker().getId());
    }

    @Test
    void shouldGetBookingByOwner() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        Booking result = bookingService.getById(booking.getId(), owner.getId());

        assertEquals(booking.getId(), result.getId());
        assertEquals(owner.getId(), result.getItem().getOwner().getId());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFoundOnGetById() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getById(booking.getId(), 99L));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenBookingNotFoundOnGetById() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getById(99L, booker.getId()));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserIsNotBookerOrOwner() {
        User otherUser = new User(3L, "Other", "other@mail.ru");

        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getById(booking.getId(), otherUser.getId()));
    }

    @Test
    void shouldGetAllBookingsByBooker() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdOrderByStartDesc(booker.getId())).thenReturn(List.of(booking));

        var result = bookingService.getByBooker(booker.getId(), BookingState.ALL);

        assertEquals(1, result.size());
    }

    @Test
    void shouldGetWaitingBookingsByBooker() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                booker.getId(),
                BookingStatus.WAITING
        )).thenReturn(List.of(booking));

        var result = bookingService.getByBooker(booker.getId(), BookingState.WAITING);

        assertEquals(1, result.size());
    }

    @Test
    void shouldGetAllBookingsByOwner() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(owner.getId())).thenReturn(List.of(booking));

        var result = bookingService.getByOwner(owner.getId(), BookingState.ALL);

        assertEquals(1, result.size());
    }

    @Test
    void shouldGetRejectedBookingsByOwner() {
        Booking rejectedBooking = new Booking(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                BookingStatus.REJECTED
        );

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                owner.getId(),
                BookingStatus.REJECTED
        )).thenReturn(List.of(rejectedBooking));

        var result = bookingService.getByOwner(owner.getId(), BookingState.REJECTED);

        assertEquals(1, result.size());
    }
}
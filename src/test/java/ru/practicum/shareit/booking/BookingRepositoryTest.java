package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тесты репозитория бронирований.
 */
@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void shouldFindBookingsByBookerId() {
        TestData data = createBooking(BookingStatus.WAITING, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        List<Booking> result = bookingRepository.findByBookerIdOrderByStartDesc(data.booker().getId());

        assertEquals(1, result.size());
        assertEquals(data.booking().getId(), result.getFirst().getId());
    }

    @Test
    void shouldFindBookingsByOwnerId() {
        TestData data = createBooking(BookingStatus.WAITING, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        List<Booking> result = bookingRepository.findByItemOwnerIdOrderByStartDesc(data.owner().getId());

        assertEquals(1, result.size());
        assertEquals(data.booking().getId(), result.getFirst().getId());
    }

    @Test
    void shouldFindPastBooking() {
        LocalDateTime now = LocalDateTime.now();
        TestData data = createBooking(BookingStatus.APPROVED, now.minusDays(3), now.minusDays(2));

        List<Booking> result = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(data.booker().getId(), now);

        assertEquals(1, result.size());
        assertEquals(data.booking().getId(), result.getFirst().getId());
    }

    @Test
    void shouldFindFutureBooking() {
        LocalDateTime now = LocalDateTime.now();
        TestData data = createBooking(BookingStatus.APPROVED, now.plusDays(1), now.plusDays(2));

        List<Booking> result = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(data.booker().getId(), now);

        assertEquals(1, result.size());
        assertEquals(data.booking().getId(), result.getFirst().getId());
    }

    @Test
    void shouldFindWaitingBookingByStatus() {
        TestData data = createBooking(BookingStatus.WAITING, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        List<Booking> result = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                data.booker().getId(),
                BookingStatus.WAITING
        );

        assertEquals(1, result.size());
        assertEquals(data.booking().getId(), result.getFirst().getId());
    }

    @Test
    void shouldFindLastApprovedBooking() {
        LocalDateTime now = LocalDateTime.now();
        TestData data = createBooking(BookingStatus.APPROVED, now.minusDays(3), now.minusDays(2));

        Optional<Booking> result = bookingRepository.findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(
                data.item().getId(),
                BookingStatus.APPROVED,
                now
        );

        assertTrue(result.isPresent());
        assertEquals(data.booking().getId(), result.get().getId());
    }

    @Test
    void shouldFindNextApprovedBooking() {
        LocalDateTime now = LocalDateTime.now();
        TestData data = createBooking(BookingStatus.APPROVED, now.plusDays(1), now.plusDays(2));

        Optional<Booking> result = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                data.item().getId(),
                BookingStatus.APPROVED,
                now
        );

        assertTrue(result.isPresent());
        assertEquals(data.booking().getId(), result.get().getId());
    }

    @Test
    void shouldCheckCompletedBookingExists() {
        LocalDateTime now = LocalDateTime.now();
        TestData data = createBooking(BookingStatus.APPROVED, now.minusDays(3), now.minusDays(2));

        boolean result = bookingRepository.existsByItemIdAndBookerIdAndEndBefore(
                data.item().getId(),
                data.booker().getId(),
                now
        );

        assertTrue(result);
    }

    private TestData createBooking(BookingStatus status, LocalDateTime start, LocalDateTime end) {
        User owner = userRepository.save(new User(null, "Owner", "owner" + System.nanoTime() + "@mail.ru"));
        User booker = userRepository.save(new User(null, "Booker", "booker" + System.nanoTime() + "@mail.ru"));
        Item item = itemRepository.save(new Item(null, "Дрель", "Хорошая дрель", true, owner, null));

        Booking booking = bookingRepository.save(new Booking(
                null,
                start,
                end,
                item,
                booker,
                status
        ));

        return new TestData(owner, booker, item, booking);
    }

    private record TestData(User owner, User booker, Item item, Booking booking) {
    }
}
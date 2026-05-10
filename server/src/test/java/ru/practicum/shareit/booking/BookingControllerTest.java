package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Owner", "owner@mail.ru");
        booker = new User(2L, "Booker", "booker@mail.ru");
        item = new Item(10L, "Дрель", "Хорошая дрель", true, owner, null);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(2);

        booking = new Booking(100L, start, end, item, booker, BookingStatus.WAITING);
    }

    @Test
    void shouldCreateBooking() throws Exception {
        BookingCreateDto createDto = new BookingCreateDto(
                10L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3)
        );

        when(bookingService.create(any(BookingCreateDto.class), eq(2L))).thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .header(USER_HEADER, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void shouldApproveBooking() throws Exception {
        Booking approved = new Booking(
                100L, booking.getStart(), booking.getEnd(), item, booker, BookingStatus.APPROVED
        );

        when(bookingService.approve(eq(100L), eq(1L), eq(true))).thenReturn(approved);

        mockMvc.perform(patch("/bookings/100")
                        .header(USER_HEADER, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void shouldGetBookingById() throws Exception {
        when(bookingService.getById(eq(100L), eq(2L))).thenReturn(booking);

        mockMvc.perform(get("/bookings/100")
                        .header(USER_HEADER, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L));
    }

    @Test
    void shouldGetBookingsByBooker() throws Exception {
        when(bookingService.getByBooker(eq(2L), eq(BookingState.ALL))).thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, 2L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(100L));
    }

    @Test
    void shouldGetBookingsByOwner() throws Exception {
        when(bookingService.getByOwner(eq(1L), eq(BookingState.ALL))).thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(100L));
    }
}

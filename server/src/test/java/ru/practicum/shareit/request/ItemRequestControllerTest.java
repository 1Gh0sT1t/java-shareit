package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private ItemRequestDto requestDto;

    @BeforeEach
    void setUp() {
        ItemRequestItemDto itemDto = new ItemRequestItemDto(100L, "Дрель", 2L);
        requestDto = new ItemRequestDto(10L, "Нужна дрель", LocalDateTime.now(), List.of(itemDto));
    }

    @Test
    void shouldCreateRequest() throws Exception {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto("Нужна дрель");

        when(itemRequestService.create(eq(1L), any(ItemRequestCreateDto.class))).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .header(USER_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.description").value("Нужна дрель"));
    }

    @Test
    void shouldGetOwnRequests() throws Exception {
        when(itemRequestService.getOwnRequests(1L)).thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests")
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(10L))
                .andExpect(jsonPath("$[0].items.length()").value(1));
    }

    @Test
    void shouldGetAllRequests() throws Exception {
        when(itemRequestService.getAllRequests(2L)).thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests/all")
                        .header(USER_HEADER, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].description").value("Нужна дрель"));
    }

    @Test
    void shouldGetRequestById() throws Exception {
        when(itemRequestService.getById(eq(2L), eq(10L))).thenReturn(requestDto);

        mockMvc.perform(get("/requests/10")
                        .header(USER_HEADER, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.items[0].name").value("Дрель"))
                .andExpect(jsonPath("$.items[0].ownerId").value(2L));
    }
}

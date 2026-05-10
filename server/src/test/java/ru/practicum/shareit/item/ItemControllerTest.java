package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void shouldCreateItem() throws Exception {
        User owner = new User(1L, "Owner", "owner@mail.ru");
        Item item = new Item(10L, "Дрель", "Хорошая дрель", true, owner, null);
        ItemDto inputDto = new ItemDto(null, "Дрель", "Хорошая дрель", true, null);

        when(itemService.create(any(Item.class), eq(1L))).thenReturn(item);

        mockMvc.perform(post("/items")
                        .header(USER_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void shouldUpdateItem() throws Exception {
        User owner = new User(1L, "Owner", "owner@mail.ru");
        Item updated = new Item(10L, "Дрель+", "Улучшенная дрель", true, owner, null);
        ItemDto inputDto = new ItemDto(null, "Дрель+", null, null, null);

        when(itemService.update(any(Item.class), eq(1L))).thenReturn(updated);

        mockMvc.perform(patch("/items/10")
                        .header(USER_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Дрель+"));
    }

    @Test
    void shouldGetItemById() throws Exception {
        ItemResponseDto responseDto = new ItemResponseDto(
                10L, "Дрель", "Хорошая дрель", true, null, null, null, List.of()
        );

        when(itemService.getById(eq(10L), eq(1L))).thenReturn(responseDto);

        mockMvc.perform(get("/items/10")
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    void shouldGetItemsByOwner() throws Exception {
        ItemResponseDto dto = new ItemResponseDto(
                10L, "Дрель", "Хорошая дрель", true, null, null, null, List.of()
        );

        when(itemService.getByOwnerId(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/items")
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(10L));
    }

    @Test
    void shouldSearchItems() throws Exception {
        User owner = new User(1L, "Owner", "owner@mail.ru");
        Item item = new Item(10L, "Дрель", "Хорошая дрель", true, owner, null);

        when(itemService.search(anyString())).thenReturn(List.of(item));

        mockMvc.perform(get("/items/search")
                        .param("text", "дрель"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Дрель"));
    }

    @Test
    void shouldAddComment() throws Exception {
        CommentDto inputDto = new CommentDto(null, "Отличная вещь", null, null);
        CommentDto savedDto = new CommentDto(1L, "Отличная вещь", "Booker", LocalDateTime.now());

        when(itemService.addComment(eq(10L), eq(2L), any(CommentDto.class))).thenReturn(savedDto);

        mockMvc.perform(post("/items/10/comment")
                        .header(USER_HEADER, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Отличная вещь"))
                .andExpect(jsonPath("$.authorName").value("Booker"));
    }
}

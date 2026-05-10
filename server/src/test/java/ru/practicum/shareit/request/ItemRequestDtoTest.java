package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void shouldSerializeWithItems() throws Exception {
        LocalDateTime created = LocalDateTime.of(2025, 5, 1, 9, 0, 0);
        ItemRequestItemDto itemDto = new ItemRequestItemDto(100L, "Дрель", 2L);
        ItemRequestDto dto = new ItemRequestDto(10L, "Нужна дрель", created, List.of(itemDto));

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPath("$.items");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Нужна дрель");
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(100);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].ownerId").isEqualTo(2);
    }

    @Test
    void shouldDeserializeFromJson() throws Exception {
        String content = """
                {
                    "id": 10,
                    "description": "Нужна дрель",
                    "created": "2025-05-01T09:00:00",
                    "items": [
                        {"id": 100, "name": "Дрель", "ownerId": 2}
                    ]
                }
                """;

        ItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getDescription()).isEqualTo("Нужна дрель");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2025, 5, 1, 9, 0, 0));
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().get(0).getId()).isEqualTo(100L);
    }

    @Test
    void shouldDeserializeWithEmptyItems() throws Exception {
        String content = """
                {
                    "id": 10,
                    "description": "Нужна дрель",
                    "created": "2025-05-01T09:00:00",
                    "items": []
                }
                """;

        ItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getItems()).isEmpty();
    }
}

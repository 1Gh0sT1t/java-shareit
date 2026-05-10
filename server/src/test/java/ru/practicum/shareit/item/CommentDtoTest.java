package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void shouldSerializeWithCorrectDateFormat() throws Exception {
        LocalDateTime created = LocalDateTime.of(2025, 6, 1, 12, 0, 0);
        CommentDto dto = new CommentDto(1L, "Отличная вещь", "Alice", created);

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).hasJsonPath("$.authorName");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Отличная вещь");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Alice");
    }

    @Test
    void shouldDeserializeFromJson() throws Exception {
        String content = """
                {
                    "id": 1,
                    "text": "Отличная вещь",
                    "authorName": "Alice",
                    "created": "2025-06-01T12:00:00"
                }
                """;

        CommentDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("Отличная вещь");
        assertThat(dto.getAuthorName()).isEqualTo("Alice");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2025, 6, 1, 12, 0, 0));
    }

    @Test
    void shouldDeserializeNullCreated() throws Exception {
        String content = """
                {
                    "id": 1,
                    "text": "Комментарий",
                    "authorName": "Bob",
                    "created": null
                }
                """;

        CommentDto dto = json.parseObject(content);

        assertThat(dto.getCreated()).isNull();
    }
}

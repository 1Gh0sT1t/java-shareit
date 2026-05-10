package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingCreateDtoTest {

    @Autowired
    private JacksonTester<BookingCreateDto> json;

    @Test
    void shouldSerializeToJson() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 6, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 6, 3, 10, 0);
        BookingCreateDto dto = new BookingCreateDto(10L, start, end);

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(10);
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
    }

    @Test
    void shouldDeserializeFromJson() throws Exception {
        String content = """
                {
                    "itemId": 10,
                    "start": "2025-06-01T10:00:00",
                    "end": "2025-06-03T10:00:00"
                }
                """;

        BookingCreateDto dto = json.parseObject(content);

        assertThat(dto.getItemId()).isEqualTo(10L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2025, 6, 1, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2025, 6, 3, 10, 0));
    }
}

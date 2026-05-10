package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для создания запроса вещи.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestCreateDto {
    // Описание того, какая вещь нужна пользователю
    @NotBlank(message = "Описание запроса не должно быть пустым")
    private String description;
}
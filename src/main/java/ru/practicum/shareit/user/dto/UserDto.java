package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO пользователя.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    // Идентификатор пользователя
    private Long id;

    // Имя пользователя
    private String name;

    // Электронная почта пользователя
    @Email(message = "Электронная почта указана неверно")
    private String email;
}
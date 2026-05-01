package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Тесты для сервиса пользователей.
 */
class UserServiceImplTest {
    // Сервис пользователей
    private UserServiceImpl userService;

    // Хранилище пользователей
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        // Создаём хранилище и сервис
        userStorage = new InMemoryUserStorage();
        userService = new UserServiceImpl(userStorage);
    }

    @Test
    void shouldCreateUser() {
        // Создаём пользователя
        User user = new User(null, "Marat", "marat@mail.ru");

        // Сохраняем пользователя
        User savedUser = userService.create(user);

        // Проверяем, что пользователь сохранился
        assertEquals(1L, savedUser.getId());
        assertEquals("Marat", savedUser.getName());
        assertEquals("marat@mail.ru", savedUser.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsBlank() {
        // Создаём пользователя с пустым email
        User user = new User(null, "Marat", "");

        // Проверяем ошибку
        assertThrows(ValidationException.class, () -> userService.create(user));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Сохраняем первого пользователя
        userService.create(new User(null, "One", "same@mail.ru"));

        // Создаём второго пользователя с тем же email
        User secondUser = new User(null, "Two", "same@mail.ru");

        // Проверяем ошибку
        assertThrows(ConflictException.class, () -> userService.create(secondUser));
    }

    @Test
    void shouldUpdateOnlyName() {
        // Сохраняем пользователя
        User savedUser = userService.create(new User(null, "Marat", "marat@mail.ru"));

        // Создаём объект для частичного обновления
        User userForUpdate = new User(savedUser.getId(), "New Name", null);

        // Обновляем пользователя
        User updatedUser = userService.update(userForUpdate);

        // Проверяем, что имя поменялось, а email остался
        assertEquals("New Name", updatedUser.getName());
        assertEquals("marat@mail.ru", updatedUser.getEmail());
    }

    @Test
    void shouldUpdateOnlyEmail() {
        // Сохраняем пользователя
        User savedUser = userService.create(new User(null, "Marat", "marat@mail.ru"));

        // Создаём объект для частичного обновления
        User userForUpdate = new User(savedUser.getId(), null, "new@mail.ru");

        // Обновляем пользователя
        User updatedUser = userService.update(userForUpdate);

        // Проверяем, что email поменялся, а имя осталось
        assertEquals("Marat", updatedUser.getName());
        assertEquals("new@mail.ru", updatedUser.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Создаём пользователя с несуществующим id
        User user = new User(999L, "Name", "mail@mail.ru");

        // Проверяем ошибку
        assertThrows(NotFoundException.class, () -> userService.update(user));
    }

    @Test
    void shouldDeleteUser() {
        // Сохраняем пользователя
        User savedUser = userService.create(new User(null, "Marat", "marat@mail.ru"));

        // Удаляем пользователя
        userService.delete(savedUser.getId());

        // Проверяем, что после удаления пользователь не находится
        assertThrows(NotFoundException.class, () -> userService.getById(savedUser.getId()));
    }
}
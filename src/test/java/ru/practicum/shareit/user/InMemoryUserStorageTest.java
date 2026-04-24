package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Тесты для хранилища пользователей в памяти.
 */
class InMemoryUserStorageTest {
    // Хранилище пользователей
    private InMemoryUserStorage userStorage;

    @BeforeEach
    void setUp() {
        // Создаём новое хранилище перед каждым тестом
        userStorage = new InMemoryUserStorage();
    }

    @Test
    void shouldCreateUser() {
        // Создаём пользователя
        User user = new User(null, "Marat", "marat@mail.ru");

        // Сохраняем пользователя
        User savedUser = userStorage.create(user);

        // Проверяем, что id назначен
        assertEquals(1L, savedUser.getId());

        // Проверяем, что пользователь сохранился
        assertEquals(savedUser, userStorage.getById(savedUser.getId()));
    }

    @Test
    void shouldUpdateUser() {
        // Сохраняем пользователя
        User savedUser = userStorage.create(new User(null, "Marat", "marat@mail.ru"));

        // Меняем данные
        savedUser.setName("Marat New");
        savedUser.setEmail("new@mail.ru");

        // Обновляем пользователя
        User updatedUser = userStorage.update(savedUser);

        // Проверяем обновление
        assertEquals("Marat New", updatedUser.getName());
        assertEquals("new@mail.ru", updatedUser.getEmail());
    }

    @Test
    void shouldReturnAllUsers() {
        // Сохраняем двух пользователей
        userStorage.create(new User(null, "One", "one@mail.ru"));
        userStorage.create(new User(null, "Two", "two@mail.ru"));

        // Получаем всех пользователей
        Collection<User> users = userStorage.getAll();

        // Проверяем размер
        assertEquals(2, users.size());
    }

    @Test
    void shouldDeleteUser() {
        // Сохраняем пользователя
        User savedUser = userStorage.create(new User(null, "Marat", "marat@mail.ru"));

        // Удаляем пользователя
        userStorage.delete(savedUser.getId());

        // Проверяем, что пользователь удалён
        assertNull(userStorage.getById(savedUser.getId()));
    }
}
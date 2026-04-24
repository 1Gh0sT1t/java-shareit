package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Collection;

/**
 * Сервис для работы с пользователями.
 */
@Service
public class UserServiceImpl implements UserService {
    // Хранилище пользователей
    private final UserStorage userStorage;

    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User create(User user) {
        // Проверяем, что email заполнен
        validateEmailForCreate(user);

        // Проверяем уникальность email перед созданием
        checkEmailUnique(user.getEmail(), null);

        // Сохраняем нового пользователя
        return userStorage.create(user);
    }

    @Override
    public User update(User user) {
        // Проверяем, что идентификатор указан
        if (user.getId() == null) {
            throw new ValidationException("Идентификатор пользователя не указан");
        }

        User savedUser = userStorage.getById(user.getId());

        // Проверяем, что пользователь существует
        if (savedUser == null) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }

        // Обновляем имя, если оно пришло
        if (user.getName() != null) {
            savedUser.setName(user.getName());
        }

        // Обновляем email, если он пришёл
        if (user.getEmail() != null) {
            if (user.getEmail().isBlank()) {
                throw new ValidationException("Электронная почта не должна быть пустой");
            }

            checkEmailUnique(user.getEmail(), savedUser.getId());
            savedUser.setEmail(user.getEmail());
        }

        // Сохраняем обновлённого пользователя
        return userStorage.update(savedUser);
    }

    @Override
    public User getById(Long userId) {
        // Получаем пользователя по id
        User user = userStorage.getById(userId);

        // Проверяем, что пользователь найден
        if (user == null) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }

        return user;
    }

    @Override
    public Collection<User> getAll() {
        // Возвращаем всех пользователей
        return userStorage.getAll();
    }

    @Override
    public void delete(Long userId) {
        // Проверяем, что пользователь найден
        if (userStorage.getById(userId) == null) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }

        // Удаляем пользователя
        userStorage.delete(userId);
    }

    // Проверяет, что email заполнен при создании
    private void validateEmailForCreate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Электронная почта не должна быть пустой");
        }
    }

    // Проверяет, что email уникален
    private void checkEmailUnique(String email, Long userId) {
        for (User savedUser : userStorage.getAll()) {
            if (savedUser.getEmail() != null
                    && savedUser.getEmail().equals(email)
                    && !savedUser.getId().equals(userId)) {
                throw new ConflictException("Пользователь с таким email уже существует");
            }
        }
    }
}
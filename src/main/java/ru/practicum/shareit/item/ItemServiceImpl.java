package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.Collection;
import java.util.Collections;

/**
 * Сервис для работы с вещами.
 */
@Service
public class ItemServiceImpl implements ItemService {
    // Хранилище вещей
    private final ItemStorage itemStorage;

    // Хранилище пользователей
    private final UserStorage userStorage;

    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Item create(Item item, Long userId) {
        // Проверяем, что владелец существует
        User owner = userStorage.getById(userId);

        if (owner == null) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }

        // Проверяем обязательные поля вещи
        validateItemForCreate(item);

        // Назначаем владельца вещи
        item.setOwner(owner);

        // Сохраняем вещь
        return itemStorage.create(item);
    }

    @Override
    public Item update(Item item, Long userId) {
        // Валидируем вещь перед обновлением
        Item savedItem = validateItemForUpdate(item, userId);

        // Обновляем название, если оно пришло
        if (item.getName() != null) {
            if (item.getName().isBlank()) {
                throw new ValidationException("Название вещи не должно быть пустым");
            }
            savedItem.setName(item.getName());
        }

        // Обновляем описание, если оно пришло
        if (item.getDescription() != null) {
            if (item.getDescription().isBlank()) {
                throw new ValidationException("Описание вещи не должно быть пустым");
            }
            savedItem.setDescription(item.getDescription());
        }

        // Обновляем статус доступности, если он пришёл
        if (item.getAvailable() != null) {
            savedItem.setAvailable(item.getAvailable());
        }

        // Сохраняем обновлённую вещь
        return itemStorage.update(savedItem);
    }

    @Override
    public Item getById(Long itemId) {
        // Получаем вещь по id
        Item item = itemStorage.getById(itemId);

        // Проверяем, что вещь найдена
        if (item == null) {
            throw new NotFoundException("Вещь с таким id не найдена");
        }

        return item;
    }

    @Override
    public Collection<Item> getByOwnerId(Long ownerId) {
        // Проверяем, что пользователь существует
        User owner = userStorage.getById(ownerId);

        if (owner == null) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }

        // Возвращаем все вещи владельца
        return itemStorage.getByOwnerId(ownerId);
    }

    @Override
    public Collection<Item> search(String text) {
        // Если строка пустая, возвращаем пустой список
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        // Ищем вещи по тексту
        return itemStorage.search(text);
    }

    // Проверяет обязательные поля вещи при создании
    private void validateItemForCreate(Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("Название вещи не должно быть пустым");
        }

        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Описание вещи не должно быть пустым");
        }

        if (item.getAvailable() == null) {
            throw new ValidationException("Статус доступности вещи должен быть указан");
        }
    }

    // Проверяет корректность данных при обновлении вещи
    private Item validateItemForUpdate(Item item, Long userId) {
        // Проверяем, что идентификатор вещи указан
        if (item.getId() == null) {
            throw new ValidationException("Идентификатор вещи не указан");
        }

        // Получаем текущую вещь
        Item savedItem = itemStorage.getById(item.getId());

        // Проверяем, что вещь существует
        if (savedItem == null) {
            throw new NotFoundException("Вещь с таким id не найдена");
        }

        // Проверяем, что вещь редактирует владелец
        if (savedItem.getOwner() == null || !savedItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Редактировать вещь может только владелец");
        }

        return savedItem;
    }
}
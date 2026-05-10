package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий запросов вещей.
 */
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
}
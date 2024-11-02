package ru.yandex.practicum.filmorate.dal.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.service.enums.EventType;
import ru.yandex.practicum.filmorate.service.enums.Operation;
import java.sql.Timestamp;

@Builder
@Data
public class Event {
    private Long id;
    private EventType eventType;
    private Operation operation;
    private Long userId;
    private Long entityId;
    private Timestamp createdAt;
}
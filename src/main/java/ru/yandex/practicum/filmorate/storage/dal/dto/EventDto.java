package ru.yandex.practicum.filmorate.storage.dal.dto;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import java.sql.Timestamp;

@Builder
@Data
public class EventDto {

    private Long id;
    private EventType eventType;
    private Operation operation;
    private Long userId;
    private Long entityId;
    private Timestamp createdAt;
}
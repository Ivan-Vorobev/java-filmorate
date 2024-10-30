package ru.yandex.practicum.filmorate.storage.dal.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import java.sql.Timestamp;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventDto {

    Long id;
    EventType eventType;
    Operation operation;
    Long userId;
    Long entityId;
    Timestamp createdAt;
}
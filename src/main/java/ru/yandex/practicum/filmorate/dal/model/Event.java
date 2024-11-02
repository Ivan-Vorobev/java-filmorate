package ru.yandex.practicum.filmorate.dal.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.dto.EventTypeDto;
import ru.yandex.practicum.filmorate.dto.OperationDto;

import java.sql.Timestamp;

@Builder
@Data
public class Event {
    private Long id;
    private EventTypeDto eventTypeDto;
    private OperationDto operationDto;
    private Long userId;
    private Long entityId;
    private Timestamp createdAt;
}
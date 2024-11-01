package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Event {

    private Long eventId;
    private Long entityId;
    private String eventType;
    private String operation;
    private Long userId;
    private Long timestamp;
}
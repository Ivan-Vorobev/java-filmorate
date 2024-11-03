package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventDto {

    private Long eventId;
    private Long entityId;
    private String eventType;
    private String operation;
    private Long userId;
    private Long timestamp;
}
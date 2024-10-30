package ru.yandex.practicum.filmorate.service.mappers;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.dal.dto.EventDto;

public class EventMapper {

    public static Event modelFromDto(EventDto eventDto) {
        return Event.builder()
                .eventId(eventDto.getId())
                .entityId(eventDto.getEntityId())
                .eventType(eventDto.getEventType().toString())
                .operation(eventDto.getOperation().toString())
                .userId(eventDto.getUserId())
                .timestamp(eventDto.getCreatedAt().toInstant().toEpochMilli())
                .build();
    }
}
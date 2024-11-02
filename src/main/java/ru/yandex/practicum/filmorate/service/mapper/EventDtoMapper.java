package ru.yandex.practicum.filmorate.service.mapper;

import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.dal.model.Event;

import java.util.Collection;

public class EventDtoMapper {

    public static EventDto modelFromDto(Event event) {
        return EventDto.builder()
                .eventId(event.getId())
                .entityId(event.getEntityId())
                .eventType(event.getEventTypeDto().toString())
                .operation(event.getOperationDto().toString())
                .userId(event.getUserId())
                .timestamp(event.getCreatedAt().toInstant().toEpochMilli())
                .build();
    }

    public static Collection<EventDto> modelFromDto(Collection<Event> event) {
        return event.stream()
                .map(EventDtoMapper::modelFromDto)
                .toList();
    }
}
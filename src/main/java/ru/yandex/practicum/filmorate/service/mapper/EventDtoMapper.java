package ru.yandex.practicum.filmorate.service.mapper;

import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.dal.model.Event;

import java.util.Collection;
import java.util.List;

public class EventDtoMapper {

    public static EventDto dtoFromModel(Event model) {
        return EventDto.builder()
                .eventId(model.getId())
                .entityId(model.getEntityId())
                .eventType(model.getEventTypeDto().toString())
                .operation(model.getOperationDto().toString())
                .userId(model.getUserId())
                .timestamp(model.getCreatedAt().toInstant().toEpochMilli())
                .build();
    }

    public static Collection<EventDto> dtoFomModel(Collection<Event> models) {
        if (models == null) {
            return List.of();
        }
        return models.stream()
                .map(EventDtoMapper::dtoFromModel)
                .toList();
    }
}
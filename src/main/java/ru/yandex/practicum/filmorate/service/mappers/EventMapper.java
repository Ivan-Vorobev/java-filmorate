package ru.yandex.practicum.filmorate.service.mappers;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.dal.dto.EventDto;
import java.util.Collection;

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

    public static Collection<Event> modelFromDto(Collection<EventDto> eventDto) {
        return eventDto.stream()
                .map(EventMapper::modelFromDto)
                .toList();
    }
}
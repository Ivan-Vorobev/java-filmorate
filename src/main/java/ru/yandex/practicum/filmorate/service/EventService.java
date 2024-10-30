package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.service.mappers.EventMapper;
import ru.yandex.practicum.filmorate.storage.dal.dto.EventDto;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;

@Service
@AllArgsConstructor
public class EventService {

    EventStorage eventStorage;

    public Collection<Event> getEventFeed(Long userId) {
        return eventStorage.getEventFeed(userId).stream()
                .map(EventMapper::modelFromDto)
                .toList();
    }

    public void add(Long entityId, Long userId, EventType eventType) {
        eventStorage.create(prepareEventDtoData(entityId, userId, eventType, Operation.ADD));
    }

    public void remove(Long entityId, Long userId, EventType eventType) {
        eventStorage.create(prepareEventDtoData(entityId, userId, eventType, Operation.REMOVE));
    }

    public void update(Long entityId, Long userId, EventType eventType) {
        eventStorage.create(prepareEventDtoData(entityId, userId, eventType, Operation.UPDATE));
    }

    private EventDto prepareEventDtoData(Long entityId, Long userId, EventType eventType, Operation operationType) {
        return EventDto.builder()
                .eventType(eventType)
                .operation(operationType)
                .userId(userId)
                .entityId(entityId)
                .createdAt(Timestamp.from(Instant.now()))
                .build();
    }
}
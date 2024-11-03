package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.service.enums.EventType;
import ru.yandex.practicum.filmorate.service.enums.Operation;
import ru.yandex.practicum.filmorate.service.mapper.EventDtoMapper;
import ru.yandex.practicum.filmorate.dal.model.Event;
import ru.yandex.practicum.filmorate.dal.model.User;
import ru.yandex.practicum.filmorate.dal.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.dal.storage.user.UserStorage;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;

@Service
public class EventService {

    private final EventStorage eventStorage;
    private final UserStorage userStorage;

    @Autowired
    public EventService(EventStorage eventStorage,
                        @Qualifier("userDbStorage") UserStorage userStorage) {
        this.eventStorage = eventStorage;
        this.userStorage = userStorage;
    }

    public Collection<EventDto> getEventFeed(Long userId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found. Id: " + userId));

        Collection<Event> eventFeed = eventStorage.getEventFeed(user.getId());
        return EventDtoMapper.dtoFomModel(eventFeed);
    }

    public void add(Long entityId, Long userId, EventType eventType) {
        eventStorage.create(prepareEventData(entityId, userId, eventType, Operation.ADD));
    }

    public void delete(Long entityId, Long userId, EventType eventType) {
        eventStorage.create(prepareEventData(entityId, userId, eventType, Operation.REMOVE));
    }

    public void update(Long entityId, Long userId, EventType eventType) {
        eventStorage.create(prepareEventData(entityId, userId, eventType, Operation.UPDATE));
    }

    private Event prepareEventData(Long entityId, Long userId, EventType eventType, Operation operationType) {
        return Event.builder()
                .eventType(eventType)
                .operation(operationType)
                .userId(userId)
                .entityId(entityId)
                .createdAt(Timestamp.from(Instant.now()))
                .build();
    }
}
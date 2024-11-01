package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.service.mappers.EventMapper;
import ru.yandex.practicum.filmorate.storage.dal.dto.EventDto;
import ru.yandex.practicum.filmorate.storage.dal.dto.UserDto;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
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

    public Collection<Event> getEventFeed(Long userId) {
        UserDto userDto = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found. Id: " + userId));

        Collection<EventDto> eventFeedDto = eventStorage.getEventFeed(userDto.getId());
        return EventMapper.modelFromDto(eventFeedDto);
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
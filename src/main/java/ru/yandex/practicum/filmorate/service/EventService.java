package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.dto.EventTypeDto;
import ru.yandex.practicum.filmorate.dto.OperationDto;
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

        Collection<Event> eventFeedDto = eventStorage.getEventFeed(user.getId());
        return EventDtoMapper.dtoFomModel(eventFeedDto);
    }

    public void add(Long entityId, Long userId, EventTypeDto eventTypeDto) {
        eventStorage.create(prepareEventDtoData(entityId, userId, eventTypeDto, OperationDto.ADD));
    }

    public void remove(Long entityId, Long userId, EventTypeDto eventTypeDto) {
        eventStorage.create(prepareEventDtoData(entityId, userId, eventTypeDto, OperationDto.REMOVE));
    }

    public void update(Long entityId, Long userId, EventTypeDto eventTypeDto) {
        eventStorage.create(prepareEventDtoData(entityId, userId, eventTypeDto, OperationDto.UPDATE));
    }

    private Event prepareEventDtoData(Long entityId, Long userId, EventTypeDto eventTypeDto, OperationDto operationDtoType) {
        return Event.builder()
                .eventTypeDto(eventTypeDto)
                .operationDto(operationDtoType)
                .userId(userId)
                .entityId(entityId)
                .createdAt(Timestamp.from(Instant.now()))
                .build();
    }
}
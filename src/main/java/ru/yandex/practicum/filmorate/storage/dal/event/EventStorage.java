package ru.yandex.practicum.filmorate.storage.dal.event;

import ru.yandex.practicum.filmorate.storage.dal.dto.EventDto;
import java.util.Collection;

public interface EventStorage {
    EventDto create(EventDto eventDto);

    Collection<EventDto> getEventFeed(Long userId);
}
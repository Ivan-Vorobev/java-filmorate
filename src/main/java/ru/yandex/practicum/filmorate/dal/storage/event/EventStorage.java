package ru.yandex.practicum.filmorate.dal.storage.event;

import ru.yandex.practicum.filmorate.dal.model.Event;

import java.util.Collection;

public interface EventStorage {
    Event create(Event event);

    Collection<Event> getEventFeed(Long userId);
}
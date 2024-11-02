package ru.yandex.practicum.filmorate.dal.storage.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.storage.BaseStorage;
import ru.yandex.practicum.filmorate.dal.model.Event;

import java.util.Collection;

@Repository
public class EventDbStorage implements EventStorage {

    private static final String INSERT_QUERY = """
            INSERT INTO events(event_type, OPERATION, user_id, entity_id, created_at)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String GET_EVENT_FEED_QUERY = """
            SELECT *
            FROM EVENTS AS e
            WHERE user_id = ?
            """;
    private final BaseStorage<Event> eventBaseStorage;

    @Autowired
    public EventDbStorage(JdbcTemplate jdbc, RowMapper<Event> eventRowMapper) {
        eventBaseStorage = new BaseStorage<>(jdbc, eventRowMapper);
    }

    @Override
    public Event create(Event event) {
        long id = eventBaseStorage.insert(
                INSERT_QUERY,
                event.getEventType().toString(),
                event.getOperation().toString(),
                event.getUserId(),
                event.getEntityId(),
                event.getCreatedAt());
        event.setId(id);
        return event;
    }

    @Override
    public Collection<Event> getEventFeed(Long userId) {
        return eventBaseStorage.findMany(GET_EVENT_FEED_QUERY, userId);
    }
}
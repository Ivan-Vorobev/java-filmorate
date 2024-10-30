package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.dal.BaseStorage;
import ru.yandex.practicum.filmorate.storage.dal.dto.EventDto;
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
               OR user_id IN
                (SELECT friend_id
                 FROM user_friends
                 WHERE user_id = ?)
            """;
    private final BaseStorage<EventDto> eventBaseStorage;

    @Autowired
    public EventDbStorage(JdbcTemplate jdbc, RowMapper<EventDto> eventDtoRowMapper) {
        eventBaseStorage = new BaseStorage<>(jdbc, eventDtoRowMapper);
    }

    @Override
    public EventDto create(EventDto eventDto) {
        long id = eventBaseStorage.insert(
                INSERT_QUERY,
                eventDto.getEventType().toString(),
                eventDto.getOperation().toString(),
                eventDto.getUserId(),
                eventDto.getEntityId(),
                eventDto.getCreatedAt());
        eventDto.setId(id);
        return eventDto;
    }

    @Override
    public Collection<EventDto> getEventFeed(Long userId) {
        return eventBaseStorage.findMany(GET_EVENT_FEED_QUERY, userId, userId);
    }
}
package ru.yandex.practicum.filmorate.storage.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.dal.dto.EventDto;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class EventDtoMapper implements RowMapper<EventDto> {
    @Override
    public EventDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EventDto.builder()
                .id(rs.getLong("id"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(Operation.valueOf(rs.getString("operation")))
                .userId(rs.getLong("user_id"))
                .entityId(rs.getLong("entity_id"))
                .createdAt(rs.getTimestamp("created_at"))
                .build();
    }
}
package ru.yandex.practicum.filmorate.storage.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.dal.dto.DirectorDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class DirectorDtoMapper implements RowMapper<DirectorDto> {
    @Override
    public DirectorDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return DirectorDto.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }
}

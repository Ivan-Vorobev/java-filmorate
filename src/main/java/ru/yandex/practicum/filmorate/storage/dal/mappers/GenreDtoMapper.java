package ru.yandex.practicum.filmorate.storage.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.dal.dto.GenreDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class GenreDtoMapper implements RowMapper<GenreDto> {
    @Override
    public GenreDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return GenreDto.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }
}

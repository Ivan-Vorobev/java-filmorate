package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.dto.RatingDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class RatingDtoMapper implements RowMapper<RatingDto> {
    @Override
    public RatingDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return RatingDto.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }
}

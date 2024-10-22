package ru.yandex.practicum.filmorate.storage.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.dal.dto.FilmLikesDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class FilmLikesDtoMapper implements RowMapper<FilmLikesDto> {
    @Override
    public FilmLikesDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return FilmLikesDto.builder()
                .filmId(rs.getLong("film_id"))
                .userId(rs.getLong("user_id"))
                .build();
    }
}

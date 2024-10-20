package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.dto.FilmGenreDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class FilmGenreDtoMapper implements RowMapper<FilmGenreDto> {
    @Override
    public FilmGenreDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return FilmGenreDto.builder()
                .filmId(rs.getLong("film_id"))
                .genreId(rs.getLong("genre_id"))
                .build();
    }
}

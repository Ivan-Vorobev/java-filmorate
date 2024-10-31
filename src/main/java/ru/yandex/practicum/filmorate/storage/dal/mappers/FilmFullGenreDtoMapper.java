package ru.yandex.practicum.filmorate.storage.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.dal.dto.FilmFullGenreDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class FilmFullGenreDtoMapper implements RowMapper<FilmFullGenreDto> {
    @Override
    public FilmFullGenreDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return FilmFullGenreDto.builder()
                .filmId(rs.getLong("film_id"))
                .genreId(rs.getLong("genre_id"))
                .genreName(rs.getString("genre_name"))
                .build();
    }
}

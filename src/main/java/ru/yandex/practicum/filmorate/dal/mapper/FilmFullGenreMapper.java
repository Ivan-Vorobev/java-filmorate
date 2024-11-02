package ru.yandex.practicum.filmorate.dal.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.model.FilmFullGenre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class FilmFullGenreMapper implements RowMapper<FilmFullGenre> {
    @Override
    public FilmFullGenre mapRow(ResultSet rs, int rowNum) throws SQLException {
        return FilmFullGenre.builder()
                .filmId(rs.getLong("film_id"))
                .genreId(rs.getLong("genre_id"))
                .genreName(rs.getString("genre_name"))
                .build();
    }
}

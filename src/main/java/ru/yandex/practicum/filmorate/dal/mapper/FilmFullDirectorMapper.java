package ru.yandex.practicum.filmorate.dal.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.model.FilmFullDirector;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class FilmFullDirectorMapper implements RowMapper<FilmFullDirector> {
    @Override
    public FilmFullDirector mapRow(ResultSet rs, int rowNum) throws SQLException {
        return FilmFullDirector.builder()
                .filmId(rs.getLong("film_id"))
                .directorId(rs.getLong("director_id"))
                .directorName(rs.getString("director_name"))
                .build();
    }
}

package ru.yandex.practicum.filmorate.storage.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.dal.dto.FilmFullDirectorDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class FilmFullDirectorDtoMapper implements RowMapper<FilmFullDirectorDto> {
    @Override
    public FilmFullDirectorDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return FilmFullDirectorDto.builder()
                .filmId(rs.getLong("film_id"))
                .directorId(rs.getLong("director_id"))
                .directorName(rs.getString("director_name"))
                .build();
    }
}

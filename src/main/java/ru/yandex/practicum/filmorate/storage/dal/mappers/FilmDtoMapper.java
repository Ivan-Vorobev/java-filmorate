package ru.yandex.practicum.filmorate.storage.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.dal.dto.FilmDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Repository
public class FilmDtoMapper implements RowMapper<FilmDto> {
    @Override
    public FilmDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return FilmDto.builder()
                .id(rs.getLong("id"))
                .ratingId(rs.getLong("rating_id"))
                .ratingName(rs.getString("rating_name"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .genres(new ArrayList<>())
                .genreId(rs.getLong("genre_id"))
                .genreName(rs.getString("genre_name"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .build();
    }
}

package ru.yandex.practicum.filmorate.dal.mapper.sub;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.model.sub.FilmLikes;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class FilmLikesMapper implements RowMapper<FilmLikes> {
    @Override
    public FilmLikes mapRow(ResultSet rs, int rowNum) throws SQLException {
        return FilmLikes.builder()
                .filmId(rs.getLong("film_id"))
                .userId(rs.getLong("user_id"))
                .build();
    }
}

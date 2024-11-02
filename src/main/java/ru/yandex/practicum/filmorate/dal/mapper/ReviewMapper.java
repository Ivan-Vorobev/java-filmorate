package ru.yandex.practicum.filmorate.dal.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class ReviewMapper implements RowMapper<Review> {
    @Override
    public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .id(rs.getLong("id"))
                .content(rs.getString("content"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .isPositive(rs.getBoolean("is_positive"))
                .useful(rs.getInt("useful"))
                .build();
    }
}
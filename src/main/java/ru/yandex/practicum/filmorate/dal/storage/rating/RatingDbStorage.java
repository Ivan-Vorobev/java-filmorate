package ru.yandex.practicum.filmorate.dal.storage.rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.storage.BaseStorage;
import ru.yandex.practicum.filmorate.dal.model.Rating;

import java.util.Collection;
import java.util.Optional;

@Repository
public class RatingDbStorage implements RatingStorage {
    private static final String FIND_RATING_QUERY = "SELECT * FROM rating WHERE id = ?";
    private static final String FIND_ALL_RATING_QUERY = "SELECT * FROM rating";
    private final BaseStorage<Rating> genreBaseStorage;

    @Autowired
    public RatingDbStorage(
            JdbcTemplate jdbc,
            RowMapper<Rating> genreRowMapper
    ) {
        genreBaseStorage = new BaseStorage<>(jdbc, genreRowMapper);
    }

    @Override
    public Collection<Rating> findAll() {
        return genreBaseStorage.findMany(FIND_ALL_RATING_QUERY);
    }

    @Override
    public Optional<Rating> findById(Long genreId) {
        return genreBaseStorage.findOne(FIND_RATING_QUERY, genreId);
    }
}

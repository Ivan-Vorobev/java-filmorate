package ru.yandex.practicum.filmorate.storage.rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.dal.BaseStorage;
import ru.yandex.practicum.filmorate.storage.dal.dto.RatingDto;

import java.util.Collection;
import java.util.Optional;

@Repository
public class RatingDbStorage implements RatingStorage {
    private static final String FIND_RATING_QUERY = "SELECT * FROM rating WHERE id = ?";
    private static final String FIND_ALL_RATING_QUERY = "SELECT * FROM rating";

    private final BaseStorage<RatingDto> genreBaseStorage;

    @Autowired
    public RatingDbStorage(
            JdbcTemplate jdbc,
            RowMapper<RatingDto> genreDtoRowMapper
    ) {
        genreBaseStorage = new BaseStorage<>(jdbc, genreDtoRowMapper);
    }

    @Override
    public Collection<RatingDto> findAll() {
        return genreBaseStorage.findMany(FIND_ALL_RATING_QUERY);
    }

    @Override
    public Optional<RatingDto> findById(Long genreId) {
        return genreBaseStorage.findOne(FIND_RATING_QUERY, genreId);
    }
}

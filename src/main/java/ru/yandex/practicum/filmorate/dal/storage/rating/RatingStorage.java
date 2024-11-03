package ru.yandex.practicum.filmorate.dal.storage.rating;

import ru.yandex.practicum.filmorate.dal.model.Rating;

import java.util.Collection;
import java.util.Optional;

public interface RatingStorage {
    Collection<Rating> findAll();

    Optional<Rating> findById(Long genreId);
}

package ru.yandex.practicum.filmorate.storage.rating;

import ru.yandex.practicum.filmorate.dal.dto.RatingDto;

import java.util.Collection;
import java.util.Optional;

public interface RatingStorage {
    Collection<RatingDto> findAll();

    Optional<RatingDto> findById(Long genreId);
}

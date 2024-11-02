package ru.yandex.practicum.filmorate.dal.storage.director;

import ru.yandex.practicum.filmorate.dal.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorStorage {
    Collection<Director> findAll();

    Optional<Director> findById(Long directorId);

    Director add(Director director);

    Director update(Director director);

    void delete(Director director);

    void addFilm(Long filmId, Long directorId);

    void deleteByFilm(Long filmId);
}

package ru.yandex.practicum.filmorate.dal.storage.director;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.storage.BaseStorage;
import ru.yandex.practicum.filmorate.dal.model.Director;

import java.util.Collection;
import java.util.Optional;

@Repository
public class DirectorDbStorage implements DirectorStorage {
    private static final String FIND_DIRECTOR_QUERY = "SELECT * FROM directorDto WHERE id = ?";
    private static final String FIND_ALL_DIRECTOR_QUERY = "SELECT * FROM directorDto";
    private static final String INSERT_DIRECTOR_QUERY = "INSERT INTO directorDto (name)" +
            "VALUES (?)";
    private static final String INSERT_FILM_DIRECTOR_QUERY = "INSERT INTO film_director (film_id, director_id) " +
            "VALUES (?, ?)";
    private static final String UPDATE_DIRECTOR_QUERY = """
            UPDATE directorDto SET name = ? WHERE id = ?
            """;
    private static final String DELETE_DIRECTOR_QUERY = "DELETE FROM directorDto WHERE id = ?";

    private static final String DELETE_FILIM_DIRECTOR_QUERY = "DELETE FROM film_director WHERE id = ?";
    private final BaseStorage<Director> directorBaseStorage;

    @Autowired
    public DirectorDbStorage(
            JdbcTemplate jdbc,
            RowMapper<Director> directorDtoRowMapper
    ) {
        directorBaseStorage = new BaseStorage<>(jdbc, directorDtoRowMapper);
    }

    @Override
    public Collection<Director> findAll() {
        return directorBaseStorage.findMany(FIND_ALL_DIRECTOR_QUERY);
    }

    @Override
    public Optional<Director> findById(Long directorId) {
        return directorBaseStorage.findOne(FIND_DIRECTOR_QUERY, directorId);
    }

    @Override
    public Director add(Director director) {
        Long id = directorBaseStorage.insert(
                INSERT_DIRECTOR_QUERY,
                director.getName()
        );
        director.setId(id);
        return director;
    }

    @Override
    public void addFilm(Long filmId, Long directorId) {
        try {
            directorBaseStorage.insert(
                    INSERT_FILM_DIRECTOR_QUERY,
                    filmId,
                    directorId
            );
        } catch (DuplicateKeyException ignored) {
        }
    }

    @Override
    public Director update(Director director) {
        directorBaseStorage.update(
                UPDATE_DIRECTOR_QUERY,
                director.getName(),
                director.getId()
        );
        return director;
    }

    @Override
    public void delete(Director director) {
        directorBaseStorage.delete(DELETE_DIRECTOR_QUERY, director.getId());
    }

    @Override
    public void deleteByFilm(Long filmId) {
        directorBaseStorage.delete(DELETE_FILIM_DIRECTOR_QUERY, filmId);
    }
}

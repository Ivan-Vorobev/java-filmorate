package ru.yandex.practicum.filmorate.storage.dal.director;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.dal.BaseStorage;
import ru.yandex.practicum.filmorate.storage.dal.dto.DirectorDto;

import java.util.Collection;
import java.util.Optional;

@Repository
public class DirectorDbStorage implements DirectorStorage {
    private static final String FIND_DIRECTOR_QUERY = "SELECT * FROM director WHERE id = ?";
    private static final String FIND_ALL_DIRECTOR_QUERY = "SELECT * FROM director";
    private static final String INSERT_DIRECTOR_QUERY = "INSERT INTO director (name)" +
            "VALUES (?)";
    private static final String INSERT_FILM_DIRECTOR_QUERY = "INSERT INTO film_director (film_id, director_id) " +
            "VALUES (?, ?)";
    private static final String UPDATE_DIRECTOR_QUERY = """
            UPDATE director SET name = ? WHERE id = ?
            """;
    private static final String DELETE_DIRECTOR_QUERY = "DELETE FROM director WHERE id = ?";

    private static final String DELETE_FILIM_DIRECTOR_QUERY = "DELETE FROM film_director WHERE id = ?";
    private final BaseStorage<DirectorDto> directorBaseStorage;

    @Autowired
    public DirectorDbStorage(
            JdbcTemplate jdbc,
            RowMapper<DirectorDto> directorDtoRowMapper
    ) {
        directorBaseStorage = new BaseStorage<>(jdbc, directorDtoRowMapper);
    }

    @Override
    public Collection<DirectorDto> findAll() {
        return directorBaseStorage.findMany(FIND_ALL_DIRECTOR_QUERY);
    }

    @Override
    public Optional<DirectorDto> findById(Long directorId) {
        return directorBaseStorage.findOne(FIND_DIRECTOR_QUERY, directorId);
    }

    @Override
    public DirectorDto add(DirectorDto directorDto) {
        Long id = directorBaseStorage.insert(
                INSERT_DIRECTOR_QUERY,
                directorDto.getName()
        );
        directorDto.setId(id);
        return directorDto;
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
    public DirectorDto update(DirectorDto directorDto) {
        directorBaseStorage.update(
                UPDATE_DIRECTOR_QUERY,
                directorDto.getName(),
                directorDto.getId()
        );
        return directorDto;
    }

    @Override
    public void delete(DirectorDto directorDto) {
        directorBaseStorage.delete(DELETE_DIRECTOR_QUERY, directorDto.getId());
    }

    @Override
    public void deleteByFilm(Long filmId) {
        directorBaseStorage.delete(DELETE_FILIM_DIRECTOR_QUERY, filmId);
    }
}

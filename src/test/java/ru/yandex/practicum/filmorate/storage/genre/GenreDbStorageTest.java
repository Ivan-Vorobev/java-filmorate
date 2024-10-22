package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.storage.dal.dto.GenreDto;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Sql({"/schema.sql", "/data.sql", "/test-data.sql"})
@DisplayName("GenreDbStorage")
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {
    private final GenreDbStorage genreDbStorage;

    @Test
    void findAll() {
        assertEquals(6, genreDbStorage.findAll().size());
    }

    @Test
    void findById() {
        Optional<GenreDto> sixGenre = genreDbStorage.findById(6L);

        assertTrue(sixGenre.isPresent());
        assertEquals(6L, sixGenre.get().getId());
        assertEquals("Боевик", sixGenre.get().getName());
    }

    @Test
    void findFilmGenres() {
        Set<Long> genres = genreDbStorage.findFilmGenres(1L);

        assertEquals(2, genres.size());
        assertArrayEquals(Arrays.asList(1L, 2L).toArray(), genres.toArray());
    }

    @Test
    void findAllFilmGenres() {
        Map<Long, Set<Long>> genres = genreDbStorage.findAllFilmGenres();

        assertEquals(2, genres.size());
        assertArrayEquals(Arrays.asList(1L, 2L).toArray(), genres.get(1L).toArray());
        assertArrayEquals(Arrays.asList(3L, 4L, 5L).toArray(), genres.get(2L).toArray());
    }

    @Test
    void delete() {
        genreDbStorage.delete(2L);

        assertEquals(0, genreDbStorage.findFilmGenres(2L).size());
    }

    @Test
    void add() {
        genreDbStorage.add(1L, 3L);
        Set<Long> genres = genreDbStorage.findFilmGenres(1L);

        assertEquals(3, genres.size());
        assertArrayEquals(Arrays.asList(1L, 2L, 3L).toArray(), genres.toArray());
    }
}
package ru.yandex.practicum.filmorate.dal.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dal.model.Film;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@ActiveProfiles("test")
@Sql({"/schema.sql", "/test-data.sql"})
@DisplayName("UserDbStorage")
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;

    private Film getFirstFilm() {
        return Film.builder()
                .id(1L)
                .ratingId(1L)
                .name("Test film 1")
                .description("Description 1")
                .genres(new ArrayList<>())
                .directors(new ArrayList<>())
                .releaseDate(LocalDate.of(2024, 1, 1))
                .duration(60)
                .build();
    }

    private Film getSecondFilm() {
        return Film.builder()
                .id(2L)
                .ratingId(2L)
                .name("Test film 2")
                .description("Description 2")
                .genres(new ArrayList<>())
                .directors(new ArrayList<>())
                .releaseDate(LocalDate.of(2024, 2, 1))
                .duration(120)
                .build();
    }

    private Film getThirdFilm() {
        return Film.builder()
                .id(3L)
                .ratingId(3L)
                .name("Test film 3")
                .description("Description 3")
                .genres(new ArrayList<>())
                .directors(new ArrayList<>())
                .releaseDate(LocalDate.of(2024, 3, 1))
                .duration(180)
                .build();
    }

    @Test
    void findAll() {
        Collection<Film> films = filmStorage.findAll();

        assertEquals(2, films.size());
        assertArrayEquals(Arrays.asList(getFirstFilm(), getSecondFilm()).toArray(), films.toArray());
    }

    @Test
    void findById() {
        Film firstFilm = getFirstFilm();
        Optional<Film> searchFilm = filmStorage.findById(1L);

        assertTrue(searchFilm.isPresent());
        assertEquals(firstFilm, searchFilm.get());
    }

    @Test
    void add() {
        Film thirdFilm = getThirdFilm();

        Film createdFilm = filmStorage.add(thirdFilm);

        assertEquals(thirdFilm.getId(), createdFilm.getId());
        assertEquals(thirdFilm.getDuration(), createdFilm.getDuration());
        assertEquals(thirdFilm.getRatingId(), createdFilm.getRatingId());
        assertEquals(thirdFilm.getName(), createdFilm.getName());
        assertEquals(thirdFilm.getReleaseDate(), createdFilm.getReleaseDate());
        assertEquals(thirdFilm.getDescription(), createdFilm.getDescription());
        assertEquals(3, filmStorage.findAll().size());

        Optional<Film> findFilm = filmStorage.findById(thirdFilm.getId());

        assertTrue(findFilm.isPresent());
        assertEquals(thirdFilm.getId(), findFilm.get().getId());
        assertEquals(thirdFilm.getDuration(), findFilm.get().getDuration());
        assertEquals(thirdFilm.getRatingId(), findFilm.get().getRatingId());
        assertEquals(thirdFilm.getName(), findFilm.get().getName());
        assertEquals(thirdFilm.getReleaseDate(), findFilm.get().getReleaseDate());
        assertEquals(thirdFilm.getDescription(), findFilm.get().getDescription());
    }

    @Test
    void update() {
        Film thirdFilm = getThirdFilm();
        thirdFilm.setId(1L);

        assertEquals(thirdFilm, filmStorage.update(thirdFilm));

        Optional<Film> findFilm = filmStorage.findById(thirdFilm.getId());
        assertTrue(findFilm.isPresent());
        assertEquals(thirdFilm, findFilm.get());
    }

    @Test
    void deleteLike() {
        filmStorage.addLike(getFirstFilm(), 1L);
        filmStorage.deleteLike(getFirstFilm(), 1L);

        Set<Long> likes = filmStorage.getFilmLikes(getFirstFilm());

        assertEquals(0, likes.size());
    }

    @Test
    void addLike() {
        filmStorage.addLike(getFirstFilm(), 1L);

        Set<Long> likes = filmStorage.getFilmLikes(getFirstFilm());

        assertEquals(1, likes.size());
    }

    @Test
    void getFilmLikes() {
        Set<Long> likes = filmStorage.getFilmLikes(getFirstFilm());

        assertEquals(0, likes.size());
    }

    @Test
    void getAllLikes() {
        filmStorage.addLike(getFirstFilm(), 1L);

        Collection<Film> likes = filmStorage.getAllLikes(1L, 0L, 0);

        assertEquals(1, likes.size());
    }
}
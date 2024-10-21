package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.storage.dal.dto.FilmDto;

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

    private FilmDto getFirstFilm() {
        return FilmDto.builder()
                .id(1L)
                .ratingId(1L)
                .name("Test film 1")
                .description("Description 1")
                .genres(new ArrayList<>())
                .genreId(0L)
                .releaseDate(LocalDate.of(2024, 1, 1))
                .duration(60)
                .build();
    }

    private FilmDto getSecondFilm() {
        return FilmDto.builder()
                .id(2L)
                .ratingId(2L)
                .name("Test film 2")
                .description("Description 2")
                .genres(new ArrayList<>())
                .genreId(0L)
                .releaseDate(LocalDate.of(2024, 2, 1))
                .duration(120)
                .build();
    }

    private FilmDto getThirdFilm() {
        return FilmDto.builder()
                .id(3L)
                .ratingId(3L)
                .name("Test film 3")
                .description("Description 3")
                .genres(new ArrayList<>())
                .genreId(0L)
                .releaseDate(LocalDate.of(2024, 3, 1))
                .duration(180)
                .build();
    }

    @Test
    void findAll() {
        Collection<FilmDto> films = filmStorage.findAll();

        assertEquals(2, films.size());
        assertArrayEquals(Arrays.asList(getFirstFilm(), getSecondFilm()).toArray(), films.toArray());
    }

    @Test
    void findById() {
        FilmDto firstFilm = getFirstFilm();
        Optional<FilmDto> searchFilm = filmStorage.findById(1L);

        assertTrue(searchFilm.isPresent());
        assertEquals(firstFilm, searchFilm.get());
    }

    @Test
    void add() {
        FilmDto thirdFilm = getThirdFilm();

        FilmDto createdFilm = filmStorage.add(thirdFilm);

        assertEquals(thirdFilm.getId(), createdFilm.getId());
        assertEquals(thirdFilm.getDuration(), createdFilm.getDuration());
        assertEquals(thirdFilm.getRatingId(), createdFilm.getRatingId());
        assertEquals(thirdFilm.getName(), createdFilm.getName());
        assertEquals(thirdFilm.getReleaseDate(), createdFilm.getReleaseDate());
        assertEquals(thirdFilm.getDescription(), createdFilm.getDescription());
        assertEquals(3, filmStorage.findAll().size());

        Optional<FilmDto> findFilm = filmStorage.findById(thirdFilm.getId());

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
        FilmDto thirdFilm = getThirdFilm();
        thirdFilm.setId(1L);

        assertEquals(thirdFilm, filmStorage.update(thirdFilm));

        Optional<FilmDto> findFilm = filmStorage.findById(thirdFilm.getId());
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
        Map<Long, Set<Long>> likes = filmStorage.getAllLikes();

        assertEquals(0, likes.size());

        filmStorage.addLike(getFirstFilm(), 1L);

        likes = filmStorage.getAllLikes();

        assertEquals(1, likes.size());
        assertEquals(1, likes.get(1L).size());
    }
}
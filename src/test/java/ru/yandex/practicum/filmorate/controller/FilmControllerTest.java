package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Фильмы")
@Sql({"/schema.sql", "/data.sql"})
@SpringBootTest
class FilmControllerTest {
    @Autowired
    private FilmController filmController;

    @Test
    @DisplayName("Успешное создание фильма")
    void add_validatePositive_allFieldsIsValid() {
        assertDoesNotThrow(
                () -> filmController.add(createFilm(1L)),
                "Валидные данные не проходят валидацию"
        );
        assertDoesNotThrow(
                () -> {
                    Film film = createFilm(null);
                    Film createdFilm = filmController.add(film);
                    assertNotEquals(null, createdFilm.getId(), "Не создано id фильма");
                    assertEquals(film.getName(), createdFilm.getName(), "Имя не совпадает");
                    assertEquals(film.getDescription(), createdFilm.getDescription(), "Описание не совпадает");
                    assertEquals(film.getDuration(), createdFilm.getDuration(), "Продолжительность не совпадает");
                    assertEquals(film.getReleaseDate(), createdFilm.getReleaseDate(), "Дата релиза не совпадает");
                },
                "Валидные данные не проходят валидацию"
        );
    }

    @Test
    @DisplayName("Успешное обновление фильма")
    void update_validatePositive_allFieldsIsValid() {
        assertDoesNotThrow(
                () -> filmController.add(createFilm(1L)),
                "Валидные данные не проходят валидацию"
        );
        assertDoesNotThrow(
                () -> {
                    Film film = createFilm(1L);
                    film.setReleaseDate(film.getReleaseDate().minusDays(1));
                    film.setDuration(film.getDuration() + 10);
                    film.setMpa(Rating.builder().id(2L).build());
                    film.setDescription(film.getDescription() + " - update description");
                    film.setName(film.getName() + " - update name");
                    Film updatedFilm = filmController.update(film);

                    assertEquals(film.getId(), updatedFilm.getId(), "ИД не изменилось");
                    assertEquals(film.getName(), updatedFilm.getName(), "Имя не изменилось");
                    assertEquals(film.getDescription(), updatedFilm.getDescription(), "Описание не изменилось");
                    assertEquals(film.getDuration(), updatedFilm.getDuration(), "Продолжительность не изменилось");
                    assertEquals(film.getReleaseDate(), updatedFilm.getReleaseDate(), "Дата релиза не изменилось");
                },
                "Валидные данные не проходят валидацию"
        );
    }

    @Test
    @DisplayName("Валидация при создании фильма")
    void add_validateNegative_fieldsIsNotValid() {
        filmValidate("add");
    }

    @Test
    @DisplayName("Валидация при обновлении фильма")
    void update_validateNegative_fieldsIsNotValid() {
        filmController.add(createFilm(1L));
        // Field #id
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    Film film = createFilm(null);
                    filmController.update(film);
                },
                "Id не должен быть null"
        );
        assertThrows(
                NotFoundException.class,
                () -> {
                    Film film = createFilm(null);
                    film.setId(Long.MAX_VALUE);
                    filmController.update(film);
                },
                "Попытка обновить несуществующий элемент"
        );
        filmValidate("update");
    }

    private void filmValidate(String method) {
        // Field #name
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    Film film = createFilm(1L);
                    film.setName(null);
                    invoke(method, film);
                },
                "Имя не должно быть null"
        );
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    Film film = createFilm(1L);
                    film.setName("   ");
                    invoke(method, film);
                },
                "Имя не должно быть пустым"
        );

        // Field #description
        assertDoesNotThrow(
                () -> {
                    Film film = createFilm(1L);
                    film.setDescription(null);
                    invoke(method, film);
                },
                "Описание может быть пустым"
        );
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    Film film = createFilm(1L);
                    StringBuilder builder = new StringBuilder();
                    builder.repeat("A", 201);
                    film.setDescription(builder.toString());
                    invoke(method, film);
                },
                "Описание должно быть не больше 200 символов"
        );
        assertDoesNotThrow(
                () -> {
                    Film film = createFilm(1L);
                    StringBuilder builder = new StringBuilder();
                    builder.repeat("A", 200);
                    film.setDescription(builder.toString());
                    invoke(method, film);
                },
                "Описание должно быть максимум 200 символов"
        );

        // Field #duration
        assertDoesNotThrow(
                () -> {
                    Film film = createFilm(1L);
                    film.setDuration(1);
                    invoke(method, film);
                },
                "Продолжительность фильма должна быть положительным числом"
        );
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    Film film = createFilm(1L);
                    film.setDuration(0);
                    invoke(method, film);
                },
                "Продолжительность фильма не равна 0"
        );
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    Film film = createFilm(1L);
                    film.setDuration(-1);
                    invoke(method, film);
                },
                "Продолжительность фильма не должна быть меньше 0"
        );

        // Field #duration
        assertDoesNotThrow(
                () -> {
                    Film film = createFilm(1L);
                    film.setReleaseDate(LocalDate.of(1895, 12, 29));
                    invoke(method, film);
                },
                "Дата релиза на день больше от разрешенной"
        );
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    Film film = createFilm(1L);
                    film.setReleaseDate(LocalDate.of(1895, 12, 28));
                    invoke(method, film);
                },
                "Дата релиза не соответствует ограничению"
        );
    }

    private void invoke(String method, Film film) throws Throwable {
        try {
            filmController
                    .getClass()
                    .getMethod(method, Film.class)
                    .invoke(filmController, film);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    private Film createFilm(Long id) {
        return Film.builder()
                .id(id)
                .name("Film #" + id)
                .description("About film #" + id)
                .mpa(Rating.builder().id(1L).build())
                .duration(180)
                .releaseDate(LocalDate.now())
                .build();
    }
}
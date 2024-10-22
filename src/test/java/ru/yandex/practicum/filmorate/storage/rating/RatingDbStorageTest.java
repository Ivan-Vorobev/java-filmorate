package ru.yandex.practicum.filmorate.storage.rating;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.storage.dal.dto.RatingDto;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Sql({"/schema.sql", "/data.sql", "/test-data.sql"})
@DisplayName("RatingDbStorage")
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RatingDbStorageTest {
    private final RatingDbStorage ratingDbStorage;

    @Test
    void findAll() {
        assertEquals(5, ratingDbStorage.findAll().size());
    }

    @Test
    void findById() {
        Optional<RatingDto> firstRating = ratingDbStorage.findById(1L);

        assertTrue(firstRating.isPresent());
        assertEquals(1L, firstRating.get().getId());
        assertEquals("G", firstRating.get().getName());
    }
}
package ru.yandex.practicum.filmorate.storage.event;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.dal.dto.EventDto;
import ru.yandex.practicum.filmorate.storage.dal.dto.UserDto;
import ru.yandex.practicum.filmorate.storage.dal.event.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.dal.user.UserDbStorage;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan("ru.yandex.practicum.filmorate")
class EventDbStorageTest {

    private final EventDbStorage eventStorage;
    private final UserDbStorage userStorage;

    @Test
    public void createEventDto() {
        Long userId = createUser();
        EventDto eventDto = EventDto.builder()
                .eventType(EventType.FRIEND)
                .operation(Operation.ADD)
                .userId(userId)
                .entityId(1L)
                .createdAt(Timestamp.from(Instant.now()))
                .build();

        assertDoesNotThrow(() -> {
            eventStorage.create(eventDto);
        });
    }

    private Long createUser() {
        Random random = new Random();
        int randomInt = random.nextInt();
        UserDto userDto = UserDto.builder()
                .email(String.format("test%d@mail.ru", randomInt))
                .login(String.format("test%d", randomInt))
                .name("test")
                .birthday(LocalDate.of(2005, 5, 1))
                .build();
        userDto = userStorage.add(userDto);
        return userDto.getId();
    }
}
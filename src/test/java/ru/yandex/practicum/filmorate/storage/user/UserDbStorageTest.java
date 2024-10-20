package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.dal.dto.UserDto;

import java.time.LocalDate;
import java.util.*;

@ActiveProfiles("test")
@Sql({"/schema.sql", "/test-data.sql"})
@DisplayName("UserDbStorage")
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    private UserDto getUser() {
        return UserDto.builder()
                .id(1L)
                .email("admin@localhost")
                .name("Admin")
                .login("admin")
                .birthday(LocalDate.of(1970, 1, 1))
                .build();
    }

    private UserDto getFriend() {
        return UserDto.builder()
                .id(2L)
                .email("moderator@localhost")
                .name("Moderator")
                .login("moderator")
                .birthday(LocalDate.of(1970, 2, 1))
                .build();
    }

    @Test
    @DisplayName("Успешное получение всех пользователей")
    void findAll() {
        Collection<UserDto> userOptional = userStorage.findAll();

        assertNotNull(userOptional, "Пользователи не найдены");
        assertEquals(2, userOptional.size(), "Должно быть два пользователя");
        assertArrayEquals(userOptional.toArray(), Arrays.asList(getUser(), getFriend()).toArray(), "Не совпадают данные в базе");
    }

    @Test
    @DisplayName("Успешное добавление друга")
    void addFriend() {
        UserDto user = getUser();

        assertEquals(0, userStorage.getFriends(user).size(), "У пользователя не должны быть друзей");

        userStorage.addFriend(user, getFriend());

        Set<Long> updatedUser = userStorage.getFriends(user);

        assertEquals(1, updatedUser.size(), "У пользователя должен появиться друг");
        assertEquals(2L, updatedUser.toArray()[0], "У пользователя должен появиться друг");
    }

    @Test
    @DisplayName("Успешное добавление нового пользователя")
    void add() {
        UserDto user = getUser();
        UserDto newUser = userStorage.add(user);

        assertEquals(3L, newUser.getId());
        assertEquals(user.getName(), newUser.getName());
        assertEquals(user.getEmail(), newUser.getEmail());
        assertEquals(user.getBirthday(), newUser.getBirthday());
        assertEquals(user.getLogin(), newUser.getLogin());
        assertEquals(3, userStorage.findAll().size());
    }

    @Test
    void update() {
        UserDto user = getUser();
        UserDto preparedToUpdateUser = UserDto.builder()
                .id(user.getId())
                .email("new-" + user.getEmail())
                .login("new-" + user.getLogin())
                .name("new-" + user.getName())
                .birthday(LocalDate.of(2024, 3, 3))
                .build();
        UserDto updatedUser = userStorage.update(preparedToUpdateUser);

        assertEquals(preparedToUpdateUser.getId(), updatedUser.getId());
        assertEquals(preparedToUpdateUser.getName(), updatedUser.getName());
        assertEquals(preparedToUpdateUser.getEmail(), updatedUser.getEmail());
        assertEquals(preparedToUpdateUser.getBirthday(), updatedUser.getBirthday());
        assertEquals(preparedToUpdateUser.getLogin(), updatedUser.getLogin());
        assertEquals(2, userStorage.findAll().size());

        Optional<UserDto> findUser = userStorage.findById(user.getId());

        assertTrue(findUser.isPresent());
        assertEquals(preparedToUpdateUser.getId(), findUser.get().getId());
        assertEquals(preparedToUpdateUser.getName(), findUser.get().getName());
        assertEquals(preparedToUpdateUser.getEmail(), findUser.get().getEmail());
        assertEquals(preparedToUpdateUser.getBirthday(), findUser.get().getBirthday());
        assertEquals(preparedToUpdateUser.getLogin(), findUser.get().getLogin());
    }

    @Test
    void findById() {
        Optional<UserDto> userOptional = userStorage.findById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void deleteFriend() {
        UserDto user = getUser();

        userStorage.addFriend(user, getFriend());
        userStorage.deleteFriend(user, getFriend());

        assertEquals(0, userStorage.getFriends(user).size());
    }

    @Test
    void getFriends() {
        UserDto user = getUser();

        userStorage.addFriend(user, getFriend());

        assertEquals(1, userStorage.getFriends(user).size());
    }
}
package ru.yandex.practicum.filmorate.dal.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.dal.model.User;

import java.time.LocalDate;
import java.util.*;

@ActiveProfiles("test")
@Sql({"/schema.sql", "/test-data.sql"})
@DisplayName("UserDbStorage")
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    private User getUser() {
        return User.builder()
                .id(1L)
                .email("admin@localhost")
                .name("Admin")
                .login("admin")
                .birthday(LocalDate.of(1970, 1, 1))
                .build();
    }

    private User getFriend() {
        return User.builder()
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
        Collection<User> userOptional = userStorage.findAll();

        assertNotNull(userOptional, "Пользователи не найдены");
        assertEquals(2, userOptional.size(), "Должно быть два пользователя");
        assertArrayEquals(userOptional.toArray(), Arrays.asList(getUser(), getFriend()).toArray(), "Не совпадают данные в базе");
    }

    @Test
    @DisplayName("Успешное добавление друга")
    void addFriend() {
        User user = getUser();

        assertEquals(0, userStorage.getFriends(user.getId()).size(), "У пользователя не должно быть друзей");

        userStorage.addFriend(user, getFriend());
        Collection<User> friends = userStorage.getFriends(user.getId());

        assertEquals(1, friends.size(), "У пользователя должен появиться друг");
        assertEquals(2L, friends.stream().toList().getFirst().getId(), "У пользователя должен появиться " +
                "друг c id = 2L");
    }

    @Test
    @DisplayName("Успешное добавление нового пользователя")
    void add() {
        User user = getUser();
        User newUser = userStorage.add(user);

        assertEquals(3L, newUser.getId());
        assertEquals(user.getName(), newUser.getName());
        assertEquals(user.getEmail(), newUser.getEmail());
        assertEquals(user.getBirthday(), newUser.getBirthday());
        assertEquals(user.getLogin(), newUser.getLogin());
        assertEquals(3, userStorage.findAll().size());
    }

    @Test
    void update() {
        User user = getUser();
        User preparedToUpdateUser = User.builder()
                .id(user.getId())
                .email("new-" + user.getEmail())
                .login("new-" + user.getLogin())
                .name("new-" + user.getName())
                .birthday(LocalDate.of(2024, 3, 3))
                .build();
        User updatedUser = userStorage.update(preparedToUpdateUser);

        assertEquals(preparedToUpdateUser.getId(), updatedUser.getId());
        assertEquals(preparedToUpdateUser.getName(), updatedUser.getName());
        assertEquals(preparedToUpdateUser.getEmail(), updatedUser.getEmail());
        assertEquals(preparedToUpdateUser.getBirthday(), updatedUser.getBirthday());
        assertEquals(preparedToUpdateUser.getLogin(), updatedUser.getLogin());
        assertEquals(2, userStorage.findAll().size());

        Optional<User> findUser = userStorage.findById(user.getId());

        assertTrue(findUser.isPresent());
        assertEquals(preparedToUpdateUser.getId(), findUser.get().getId());
        assertEquals(preparedToUpdateUser.getName(), findUser.get().getName());
        assertEquals(preparedToUpdateUser.getEmail(), findUser.get().getEmail());
        assertEquals(preparedToUpdateUser.getBirthday(), findUser.get().getBirthday());
        assertEquals(preparedToUpdateUser.getLogin(), findUser.get().getLogin());
    }

    @Test
    void findById() {
        Optional<User> userOptional = userStorage.findById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void deleteFriend() {
        User user = getUser();
        userStorage.addFriend(user, getFriend());
        userStorage.deleteFriend(user, getFriend());

        assertEquals(0, userStorage.getFriends(user.getId()).size());
    }

    @Test
    void getFriends() {
        User user = getUser();
        userStorage.addFriend(user, getFriend());

        assertEquals(1, userStorage.getFriends(user.getId()).size());
    }
}
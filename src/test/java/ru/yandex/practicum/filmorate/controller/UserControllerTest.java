package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Пользователи")
@Sql({"/schema.sql", "/data.sql"})
@SpringBootTest
class UserControllerTest {
    @Autowired
    private UserController userController;

    @Test
    @DisplayName("Успешное создание пользователя")
    void add_validatePositive_allFieldsIsValid() {
        assertDoesNotThrow(
                () -> userController.add(createUser(1L)),
                "Валидные данные не проходят валидацию"
        );
        assertDoesNotThrow(
                () -> {
                    User user = createUser(null);
                    User createdUser = userController.add(user);
                    assertNotEquals(null, createdUser.getId(), "Не создано id пользователя");
                    assertEquals(user.getName(), createdUser.getName(), "Имя не совпадает");
                    assertEquals(user.getLogin(), createdUser.getLogin(), "Login не совпадает");
                    assertEquals(user.getEmail(), createdUser.getEmail(), "Email не совпадает");
                    assertEquals(user.getBirthday(), createdUser.getBirthday(), "Дата рождения не совпадает");
                },
                "Валидные данные не проходят валидацию"
        );
    }

    @Test
    @DisplayName("Успешное обновление пользователя")
    void update_validatePositive_allFieldsIsValid() {
        assertDoesNotThrow(
                () -> userController.add(createUser(1L)),
                "Валидные данные не проходят валидацию"
        );
        assertDoesNotThrow(
                () -> {
                    User user = createUser(1L);
                    user.setBirthday(user.getBirthday().minusDays(1));
                    user.setLogin(user.getLogin() + "_upl");
                    user.setEmail("Test_" + user.getId() + "_updated@updated.com");
                    user.setName(user.getName() + " - update name");
                    User updateduserUser = userController.update(user);

                    assertEquals(user.getId(), updateduserUser.getId(), "ИД не изменилось");
                    assertEquals(user.getName(), updateduserUser.getName(), "Имя не изменилось");
                    assertEquals(user.getBirthday(), updateduserUser.getBirthday(), "Дата рождения не изменилась");
                    assertEquals(user.getEmail(), updateduserUser.getEmail(), "Email не изменился");
                    assertEquals(user.getLogin(), updateduserUser.getLogin(), "Login не изменился");
                },
                "Валидные данные не проходят валидацию"
        );
    }

    @Test
    @DisplayName("Валидация при создании пользователя")
    void add_validateNegative_fieldsIsNotValid() {
        userValidate("add");
    }

    @Test
    @DisplayName("Валидация при обновлении пользователя")
    void update_validateNegative_fieldsIsNotValid() {
        userController.add(createUser(1L));
        // Field #id
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    User user = createUser(null);
                    userController.update(user);
                },
                "Id не должен быть null"
        );
        assertThrows(
                NotFoundException.class,
                () -> {
                    User user = createUser(null);
                    user.setId(Long.MAX_VALUE);
                    userController.update(user);
                },
                "Попытка обновить несуществующий элемент"
        );
        userValidate("update");
    }

    private void userValidate(String method) {
        // Field #name
        assertDoesNotThrow(
                () -> {
                    User user = createUser(1L);
                    user.setName(null);
                    User createdUser = invoke(method, user);
                    assertEquals(createdUser.getLogin(), createdUser.getName(), "При пустом имени getName не возвращает login");
                },
                "Имя необязательное поле"
        );
        assertDoesNotThrow(
                () -> {
                    User user = createUser(1L);
                    user.setName("  ");
                    User createdUser = invoke(method, user);
                    assertEquals(createdUser.getLogin(), createdUser.getName(), "При пустом имени getName не возвращает login");
                },
                "Имя необязательное поле"
        );

        // Field #login
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    User user = createUser(1L);
                    user.setLogin(null);
                    invoke(method, user);
                },
                "Login не может быть пустым"
        );
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    User user = createUser(1L);
                    user.setLogin("  ");
                    invoke(method, user);
                },
                "Login не может содержать только пробелы"
        );

        // Field #ууьфшд
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    User user = createUser(1L);
                    user.setEmail("   ");
                    invoke(method, user);
                },
                "Email не может содержать только пробелы"
        );
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    User user = createUser(1L);
                    user.setEmail(null);
                    invoke(method, user);
                },
                "Email не может быть пустым"
        );
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    User user = createUser(1L);
                    user.setEmail("new_email.ru");
                    invoke(method, user);
                },
                "Невалидный адрес email"
        );

        // Field #birthday
        assertDoesNotThrow(
                () -> {
                    User user = createUser(1L);
                    user.setBirthday(LocalDate.now());
                    invoke(method, user);
                },
                "Дата рождения может быть now()"
        );
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    User user = createUser(1L);
                    user.setBirthday(LocalDate.now().plusDays(1));
                    invoke(method, user);
                },
                "Дата рождения не может быть из будущего"
        );
    }

    private User invoke(String method, User user) throws Throwable {
        try {
            return (User) userController
                    .getClass()
                    .getMethod(method, User.class)
                    .invoke(userController, user);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    private User createUser(Long id) {
        return User.builder()
                .id(id)
                .name("User #" + id)
                .email(id + "test@mail.ru")
                .login("Login_" + id)
                .birthday(LocalDate.of(1988, 1, 1))
                .build();
    }
}
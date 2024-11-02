package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.dto.UserDto;

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
                () -> userController.create(createUser(1L)),
                "Валидные данные не проходят валидацию"
        );
        assertDoesNotThrow(
                () -> {
                    UserDto userDto = createUser(null);
                    UserDto createdUserDto = userController.create(userDto);
                    assertNotEquals(null, createdUserDto.getId(), "Не создано id пользователя");
                    assertEquals(userDto.getName(), createdUserDto.getName(), "Имя не совпадает");
                    assertEquals(userDto.getLogin(), createdUserDto.getLogin(), "Login не совпадает");
                    assertEquals(userDto.getEmail(), createdUserDto.getEmail(), "Email не совпадает");
                    assertEquals(userDto.getBirthday(), createdUserDto.getBirthday(), "Дата рождения не совпадает");
                },
                "Валидные данные не проходят валидацию"
        );
    }

    @Test
    @DisplayName("Успешное обновление пользователя")
    void update_validatePositive_allFieldsIsValid() {
        assertDoesNotThrow(
                () -> userController.create(createUser(1L)),
                "Валидные данные не проходят валидацию"
        );
        assertDoesNotThrow(
                () -> {
                    UserDto userDto = createUser(1L);
                    userDto.setBirthday(userDto.getBirthday().minusDays(1));
                    userDto.setLogin(userDto.getLogin() + "_upl");
                    userDto.setEmail("Test_" + userDto.getId() + "_updated@updated.com");
                    userDto.setName(userDto.getName() + " - update name");
                    UserDto updateduserUserDto = userController.update(userDto);

                    assertEquals(userDto.getId(), updateduserUserDto.getId(), "ИД не изменилось");
                    assertEquals(userDto.getName(), updateduserUserDto.getName(), "Имя не изменилось");
                    assertEquals(userDto.getBirthday(), updateduserUserDto.getBirthday(), "Дата рождения не изменилась");
                    assertEquals(userDto.getEmail(), updateduserUserDto.getEmail(), "Email не изменился");
                    assertEquals(userDto.getLogin(), updateduserUserDto.getLogin(), "Login не изменился");
                },
                "Валидные данные не проходят валидацию"
        );
    }

    @Test
    @DisplayName("Валидация при создании пользователя")
    void add_validateNegative_fieldsIsNotValid() {
        userValidate("create");
    }

    @Test
    @DisplayName("Валидация при обновлении пользователя")
    void update_validateNegative_fieldsIsNotValid() {
        userController.create(createUser(1L));
        // Field #id
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    UserDto userDto = createUser(null);
                    userController.update(userDto);
                },
                "Id не должен быть null"
        );
        assertThrows(
                NotFoundException.class,
                () -> {
                    UserDto userDto = createUser(null);
                    userDto.setId(Long.MAX_VALUE);
                    userController.update(userDto);
                },
                "Попытка обновить несуществующий элемент"
        );
        userValidate("update");
    }

    private void userValidate(String method) {
        // Field #name
        assertDoesNotThrow(
                () -> {
                    UserDto userDto = createUser(1L);
                    userDto.setName(null);
                    UserDto createdUserDto = invoke(method, userDto);
                    assertEquals(createdUserDto.getLogin(), createdUserDto.getName(), "При пустом имени getName не возвращает login");
                },
                "Имя необязательное поле"
        );
        assertDoesNotThrow(
                () -> {
                    UserDto userDto = createUser(1L);
                    userDto.setName("  ");
                    UserDto createdUserDto = invoke(method, userDto);
                    assertEquals(createdUserDto.getLogin(), createdUserDto.getName(), "При пустом имени getName не возвращает login");
                },
                "Имя необязательное поле"
        );

        // Field #login
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    UserDto userDto = createUser(1L);
                    userDto.setLogin(null);
                    invoke(method, userDto);
                },
                "Login не может быть пустым"
        );
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    UserDto userDto = createUser(1L);
                    userDto.setLogin("  ");
                    invoke(method, userDto);
                },
                "Login не может содержать только пробелы"
        );

        // Field #ууьфшд
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    UserDto userDto = createUser(1L);
                    userDto.setEmail("   ");
                    invoke(method, userDto);
                },
                "Email не может содержать только пробелы"
        );
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    UserDto userDto = createUser(1L);
                    userDto.setEmail(null);
                    invoke(method, userDto);
                },
                "Email не может быть пустым"
        );
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    UserDto userDto = createUser(1L);
                    userDto.setEmail("new_email.ru");
                    invoke(method, userDto);
                },
                "Невалидный адрес email"
        );

        // Field #birthday
        assertDoesNotThrow(
                () -> {
                    UserDto userDto = createUser(1L);
                    userDto.setBirthday(LocalDate.now());
                    invoke(method, userDto);
                },
                "Дата рождения может быть now()"
        );
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    UserDto userDto = createUser(1L);
                    userDto.setBirthday(LocalDate.now().plusDays(1));
                    invoke(method, userDto);
                },
                "Дата рождения не может быть из будущего"
        );
    }

    private UserDto invoke(String method, UserDto userDto) throws Throwable {
        try {
            return (UserDto) userController
                    .getClass()
                    .getMethod(method, UserDto.class)
                    .invoke(userController, userDto);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    private UserDto createUser(Long id) {
        return UserDto.builder()
                .id(id)
                .name("User #" + id)
                .email(id + "test@mail.ru")
                .login("Login_" + id)
                .birthday(LocalDate.of(1988, 1, 1))
                .build();
    }
}
package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.RequestMethod;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;
import java.util.Collection;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;
    private final FilmService filmService;
    private final EventService eventService;

    @GetMapping
    public Collection<UserDto> findAll() {
        return userService.findAll();
    }

    @PostMapping
    @Validated(RequestMethod.Create.class)
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @PutMapping
    @Validated(RequestMethod.Update.class)
    public UserDto update(@Valid @RequestBody UserDto userDto) {
        UserDto updatedUserDto;

        updatedUserDto = userService.update(userDto);

        log.info("User updated. Id: " + updatedUserDto.getId());
        return updatedUserDto;
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable("id") Long id) {
        log.info("Поступил запрос GET на получение данных о пользователе с id = {}", id);
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void removeUserById(@PathVariable("id") Long id) {
        log.info("Получен запрос DELETE на удаление пользователя с id = {}", id);
        userService.removeUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable("id") Long userId,
            @PathVariable("friendId") Long friendId
    ) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(
            @PathVariable("id") Long userId,
            @PathVariable("friendId") Long friendId
    ) {
        userService.deleteFriend(userId, friendId);

    }

    @GetMapping("/{id}/friends")
    public Collection<UserDto> findFriends(
            @PathVariable("id") Long userId
    ) {
        return userService.findFriends(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<UserDto> findCommonFriends(
            @PathVariable("id") Long userId,
            @PathVariable("otherId") Long otherId
    ) {
        return userService.findCommonFriends(userId, otherId);
    }

    @GetMapping("/{id}/recommendations")
    public Collection<FilmDto> getRecommendations(@PathVariable("id") Long userId) {
        return filmService.getRecommendations(userId);
    }

    @GetMapping("/{id}/feed")
    public Collection<EventDto> getEventFeed(@PathVariable("id") Long userId) {
        return eventService.getEventFeed(userId);
    }
}
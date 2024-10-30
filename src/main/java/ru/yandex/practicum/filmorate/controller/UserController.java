package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.RequestMethod;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
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
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @PostMapping
    @Validated(RequestMethod.Create.class)
    public User add(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    @Validated(RequestMethod.Update.class)
    public User update(@Valid @RequestBody User user) {
        User updatedUser;

        updatedUser = userService.update(user);

        log.info("User updated. Id: " + updatedUser.getId());
        return updatedUser;
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
    public Collection<User> findUsers(
            @PathVariable("id") Long userId
    ) {
        return userService.findFriends(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> findUsers(
            @PathVariable("id") Long userId,
            @PathVariable("otherId") Long otherId
    ) {
        return userService.findCommonFriends(userId, otherId);
    }

    @GetMapping("/{id}/recommendations")
    public Collection<Film> getRecommendations(@PathVariable("id") Long userId) {
        return filmService.getRecommendations(userId);
    }

    @GetMapping("/{id}/feed")
    public Collection<Event> getEventFeed(@PathVariable("id") Long userId) {
        log.info("Поступил запрос GET на получение ленты пользователя {}", userId);
        User user = userService.findUser(userId);
        return eventService.getEventFeed(user.getId());
    }
}

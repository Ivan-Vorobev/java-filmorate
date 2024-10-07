package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.RequestMethod;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @PostMapping
    @Validated(RequestMethod.Create.class)
    public User add(@Valid @RequestBody User user) {
        userService.create(user);
        return user;
    }

    @PutMapping
    @Validated(RequestMethod.Update.class)
    public User update(@Valid @RequestBody User user) {
        User updatedUser;

        try {
            updatedUser = userService.update(user);
        } catch (NotFoundException e) {
            log.info(e.getMessage());
            throw e;
        }

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
}

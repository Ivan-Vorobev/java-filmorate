package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.RequestMethod;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.GenerateIdService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private final GenerateIdService idGenerator = new GenerateIdService();

    @GetMapping
    public Collection<User> findAll() {
        log.info("GET /users");
        return users.values();
    }

    @PostMapping
    @Validated(RequestMethod.Create.class)
    public User add(@Valid @RequestBody User user) {
        log.info("POST /users");
        user.setId(idGenerator.generate());
        users.put(user.getId(), user);
        log.info("Add user with id: " + user.getId());
        return user;
    }

    @PutMapping
    @Validated(RequestMethod.Update.class)
    public User update(@Valid @RequestBody User user) {
        log.info("PUT /users");
        User searchUser = users.get(user.getId());

        if (searchUser == null) {
            String errorMessage = "User not found. Id: " + user.getId();
            log.info(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        users.put(user.getId(), user);

        log.info("User updated. Id: " + user.getId());
        return user;
    }
}

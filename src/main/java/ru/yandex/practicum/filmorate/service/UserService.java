package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.dal.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.dal.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.service.enums.EventType;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.dal.model.User;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.mapper.UserDtoMapper;
import ru.yandex.practicum.filmorate.dal.storage.user.UserStorage;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;
    private final EventService eventService;
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final EventStorage eventStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       EventService eventService,
                       ReviewStorage reviewStorage,
                       FilmStorage filmStorage,
                       EventStorage eventStorage) {
        this.userStorage = userStorage;
        this.eventService = eventService;
        this.reviewStorage = reviewStorage;
        this.filmStorage = filmStorage;
        this.eventStorage = eventStorage;
    }

    public Collection<UserDto> findAll() {
        return userStorage.findAll().stream()
                .map(UserDtoMapper::dtoFromModel)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long userId) {
        User user = userStorage
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found. Id: " + userId));

        return UserDtoMapper.dtoFromModel(user);
    }

    public UserDto create(UserDto userDto) {
        return UserDtoMapper.dtoFromModel(
                userStorage.add(
                        UserDtoMapper.modelFromDto(userDto)
                )
        );
    }

    public UserDto update(UserDto userDto) {
        getUserById(userDto.getId());
        return UserDtoMapper.dtoFromModel(
                userStorage.update(
                        UserDtoMapper.modelFromDto(userDto)
                )
        );
    }

    public void deleteUserById(Long userId) {
        UserDto user = getUserById(userId);
        userStorage.deleteUserById(user.getId());
        userStorage.deleteUserFromFriends(user.getId());
        filmStorage.deleteLikeByUserId(user.getId());
        reviewStorage.deleteReviewByUserId(user.getId());
        reviewStorage.deleteReviewRatingsByUserId(user.getId());
        eventStorage.deleteEventByUserId(user.getId());
    }

    public void addFriend(Long userId, Long friendId) {
        if (Objects.equals(userId, friendId)) {
            throw new IllegalArgumentException("Пользователь и друг совпадают");
        }
        UserDto userDto = getUserById(userId);
        UserDto friend = getUserById(friendId);
        userStorage.addFriend(UserDtoMapper.modelFromDto(userDto), UserDtoMapper.modelFromDto(friend));
        eventService.add(friendId, userId, EventType.FRIEND);
    }

    public void deleteFriend(Long userId, Long friendId) {
        if (Objects.equals(userId, friendId)) {
            throw new IllegalArgumentException("Пользователь и друг совпадают");
        }
        UserDto userDto = getUserById(userId);
        UserDto friend = getUserById(friendId);
        userStorage.deleteFriend(UserDtoMapper.modelFromDto(userDto), UserDtoMapper.modelFromDto(friend));
        eventService.delete(friendId, userId, EventType.FRIEND);
    }

    public Collection<UserDto> findFriends(Long userId) {
        UserDto user = getUserById(userId);
        return UserDtoMapper.dtoFromModel(userStorage.getFriends(user.getId()));
    }

    public Collection<UserDto> findCommonFriends(final Long userId, final Long otherId) {
        UserDto userDto = getUserById(userId);
        UserDto otherUserDto = getUserById(otherId);
        Collection<User> commonFriendsOfUsers = userStorage.getCommonFriendsOfUsers(userDto.getId(), otherUserDto.getId());
        return UserDtoMapper.dtoFromModel(commonFriendsOfUsers);
    }
}
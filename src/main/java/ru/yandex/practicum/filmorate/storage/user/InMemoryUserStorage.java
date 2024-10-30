package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.dal.dto.UserDto;
import ru.yandex.practicum.filmorate.storage.GenerateIdStorage;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, UserDto> users = new HashMap<>();
    private final Map<Long, Set<Long>> friends = new HashMap<>();
    private final GenerateIdStorage idGenerator;

    public Collection<UserDto> findAll() {
        return users.values();
    }

    @Override
    public Optional<UserDto> findById(Long id) {
        UserDto user = users.get(id);
        return user == null
                ? Optional.empty()
                : Optional.of(user);
    }

    @Override
    public void addFriend(UserDto user, UserDto friend) {
        Set<Long> friendsIds = friends.get(user.getId());
        if (friendsIds == null) {
            friendsIds = new HashSet<>();
            friendsIds.add(friend.getId());
            friends.put(user.getId(), friendsIds);
        } else {
            if (!friendsIds.contains(friend.getId())) {
                friendsIds.add(friend.getId());
                friends.put(user.getId(), friendsIds);
            }
        }
    }

    @Override
    public void deleteFriend(UserDto user, UserDto friend) {
        Set<Long> friendsIds = friends.get(user.getId());
        if (friendsIds.contains(friend.getId())) {
            friendsIds.remove(friend.getId());
            friends.put(user.getId(), friendsIds);
        }
    }

    @Override
    public Collection<UserDto> getFriends(Long userId) {
        Set<Long> userFriendsIds = friends.get(userId);
        return userFriendsIds.stream()
                .map(users::get)
                .toList();
    }

    @Override
    public Collection<UserDto> getCommonFriendsOfUsers(Long userId, Long otherId) {
        Set<Long> userFriendsIds = friends.get(userId);
        Set<Long> otherFriendsIds = friends.get(otherId);
        Set<Long> listOfCommonFriendsIds = userFriendsIds.stream()
                .filter(otherFriendsIds::contains)
                .collect(Collectors.toSet());
        return listOfCommonFriendsIds.stream()
                .map(users::get)
                .toList();
    }

    @Override
    public UserDto add(UserDto user) {
        user.setId(idGenerator.generate());
        users.put(user.getId(), user);
        friends.put(user.getId(), new HashSet<>());
        return user;
    }

    @Override
    public UserDto update(UserDto user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void removeUserById(Long userId) {
        users.remove(userId);
        friends.remove(userId);
    }
}
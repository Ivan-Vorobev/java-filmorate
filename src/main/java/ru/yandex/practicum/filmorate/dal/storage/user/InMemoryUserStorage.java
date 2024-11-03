package ru.yandex.practicum.filmorate.dal.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.model.User;
import ru.yandex.practicum.filmorate.dal.storage.GenerateIdStorage;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Set<Long>> friends = new HashMap<>();
    private final GenerateIdStorage idGenerator;

    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public Optional<User> findById(Long id) {
        User user = users.get(id);
        return user == null
                ? Optional.empty()
                : Optional.of(user);
    }

    @Override
    public void addFriend(User user, User friend) {
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
    public void deleteFriend(User user, User friend) {
        Set<Long> friendsIds = friends.get(user.getId());
        if (friendsIds.contains(friend.getId())) {
            friendsIds.remove(friend.getId());
            friends.put(user.getId(), friendsIds);
        }
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        Set<Long> userFriendsIds = friends.get(userId);
        return userFriendsIds.stream()
                .map(users::get)
                .toList();
    }

    @Override
    public Collection<User> getCommonFriendsOfUsers(Long userId, Long otherId) {
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
    public User add(User user) {
        user.setId(idGenerator.generate());
        users.put(user.getId(), user);
        friends.put(user.getId(), new HashSet<>());
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUserById(Long userId) {
        users.remove(userId);
        friends.remove(userId);
    }

    @Override
    public void deleteUserFromFriends(Long userId) {
        friends.remove(userId);
    }
}
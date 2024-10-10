package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.GenerateIdStorage;

import java.util.*;

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
    public User findById(Long id) {
        return users.get(id);
    }

    @Override
    public User addFriend(User user, User friend) {
        Set<Long> userFriends = this.getFriends(user);
        Set<Long> friendFriends = this.getFriends(friend);

        userFriends.add(friend.getId());
        friendFriends.add(user.getId());

        return user;
    }

    @Override
    public User deleteFriend(User user, User friend) {
        Set<Long> userFriends = this.getFriends(user);
        Set<Long> friendFriends = this.getFriends(friend);

        userFriends.remove(friend.getId());
        friendFriends.remove(user.getId());

        return user;
    }

    @Override
    public Set<Long> getFriends(User user) {
        return friends.get(user.getId());
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
}

package ru.yandex.practicum.filmorate.dal.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.storage.BaseStorage;
import ru.yandex.practicum.filmorate.dal.model.User;
import ru.yandex.practicum.filmorate.dal.model.sub.UserFriends;
import java.util.Collection;
import java.util.Optional;

@Repository
@Primary
public class UserDbStorage implements UserStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_USER_QUERY = "INSERT INTO users (email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_QUERY = """
            UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?
            """;
    private static final String DELETE_USER_BY_ID_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String DELETE_FROM_USER_FRIENDS_BY_FRIEND_ID_QUERY = "DELETE FROM user_friends WHERE friend_id = ?";
    private static final String INSERT_FRIEND_QUERY = "INSERT INTO user_friends (user_id, friend_id) VALUES (?, ?)";
    private static final String DELETE_USER_FRIEND_QUERY = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
    private static final String FIND_USER_LIKES_QUERY = """
                SELECT id,
                       email,
                       login,
                       name,
                       birthday
                FROM users
                WHERE id IN
                    (SELECT friend_id
                     FROM user_friends
                     WHERE user_id = ?)
            """;
    private static final String FIND_ALL_COMMON_USER_FRIENDS_QUERY = """
            SELECT id,
                   email,
                   login,
                   name,
                   birthday
            FROM users
            WHERE id IN
                (SELECT friend_id AS user_id
                 FROM user_friends
                 WHERE user_id = ?)
              AND id IN
                (SELECT friend_id AS user_id
                 FROM user_friends
                 WHERE user_id = ?)
            """;
    private final BaseStorage<User> userBaseStorage;
    private final BaseStorage<UserFriends> userFriendBaseStorage;

    public UserDbStorage(
            JdbcTemplate jdbc,
            @Qualifier("userMapper") RowMapper<User> userRowMapper,
            @Qualifier("userFriendsMapper") RowMapper<UserFriends> userFriendRowMapper
    ) {
        userBaseStorage = new BaseStorage<>(jdbc, userRowMapper);
        userFriendBaseStorage = new BaseStorage<>(jdbc, userFriendRowMapper);
    }

    @Override
    public Collection<User> findAll() {
        return userBaseStorage.findMany(FIND_ALL_QUERY);
    }

    @Override
    public void addFriend(User user, User friend) {
        try {
            userFriendBaseStorage.insert(INSERT_FRIEND_QUERY, user.getId(), friend.getId());
        } catch (Exception ignored) {
        }
    }

    @Override
    public User add(User user) {
        Long id = userBaseStorage.insert(
                INSERT_USER_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        userBaseStorage.update(
                UPDATE_USER_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public void deleteUserById(Long userId) {
        userBaseStorage.delete(DELETE_USER_BY_ID_QUERY, userId);
    }

    @Override
    public void deleteUserFromFriends(Long userId) {
        userBaseStorage.delete(DELETE_FROM_USER_FRIENDS_BY_FRIEND_ID_QUERY, userId);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userBaseStorage.findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public void deleteFriend(User user, User friend) {
        userFriendBaseStorage.delete(DELETE_USER_FRIEND_QUERY, user.getId(), friend.getId());
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        return userBaseStorage.findMany(FIND_USER_LIKES_QUERY, userId);
    }

    @Override
    public Collection<User> getCommonFriendsOfUsers(Long userId, Long otherId) {
        return userBaseStorage.findMany(FIND_ALL_COMMON_USER_FRIENDS_QUERY, userId, otherId);
    }
}
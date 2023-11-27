package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.exceptions.IncorrectObjectModificationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.DBFriendsStorage;
import ru.yandex.practicum.filmorate.storage.DBLikesStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final DBFriendsStorage friendsStorage;
    private final DBLikesStorage likesStorage;

    @Autowired
    public UserService(@Qualifier("dBUserStorage") UserStorage userStorage,
                       DBFriendsStorage friendsStorage,
                       DBLikesStorage likesStorage) {
        this.userStorage = userStorage;
        this.friendsStorage = friendsStorage;
        this.likesStorage = likesStorage;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userStorage.getAllUsers().values());
    }

    public User getUser(int userId) {
        return userStorage.getUser(userId);
    }

    public List<User> getFriends(int userId) {
        // проверка на существование User
        userStorage.getUser(userId);

        Set<Integer> friendsId = friendsStorage.getFriends(userId);
        List<User> friends = new ArrayList<>();
        Map<Integer, User> users = userStorage.getAllUsers();

        for (int id : friendsId) {
            friends.add(users.get(id));
        }

        return friends;
    }

    public List<User> getSameFriends(int userId, int otherId) {
        // проверка на существование User
        userStorage.getUser(userId);
        userStorage.getUser(otherId);

        Set<Integer> userFriends = friendsStorage.getFriends(userId);
        userFriends.retainAll(friendsStorage.getFriends(otherId));

        List<User> sameFriends = new ArrayList<>();
        Map<Integer, User> users = userStorage.getAllUsers();

        for (int id : userFriends) {
            sameFriends.add(users.get(id));
        }

        return sameFriends;
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User addFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new IncorrectObjectModificationException("Невозможно добавить пользователя в друзья т.к. id совпадают"
                    + " userId = " + userId + ", friendId = " + friendId);
        }

        // проверка на существование пользователей
        User user = userStorage.getUser(userId);
        userStorage.getUser(friendId);

        if (friendsStorage.getFriends(userId).contains(friendId)) {
            throw new IncorrectObjectModificationException("Данный пользователь с friendId = " + friendId
                    + " уже добавлен в друзья пользователя с userId = " + userId);
        }

        friendsStorage.addFriend(userId, friendId);
        user.setFriends(new HashSet<>(friendsStorage.getFriends(userId)));

        return user;
    }

    public User deleteUser(int userId) {
        User user = userStorage.deleteUser(userId);
        user.setFriends(friendsStorage.getFriends(userId));
        friendsStorage.deleteFriends(userId);
        likesStorage.deleteUserLikes(userId);
        return user;
    }

    public User deleteFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new IncorrectObjectModificationException("Невозможно удалить пользователя из друзей т.к. id совпадают"
                    + " userId = " + userId + ", friendsId = " + friendId);
        }

        // проверка на существование пользователей
        User user = userStorage.getUser(userId);
        userStorage.getUser(friendId);

        if (!friendsStorage.getFriends(userId).contains(friendId)) {
            throw new IncorrectObjectModificationException("Данный пользователь с friendId = " + friendId
                    + " не находится в друзья пользователя с userId = " + userId);
        }

        friendsStorage.deleteFriend(userId, friendId);
        user.setFriends(friendsStorage.getFriends(userId));

        return user;
    }
}
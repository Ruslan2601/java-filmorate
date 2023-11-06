package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.exceptions.IncorrectObjectModificationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userStorage.getAllUsers().values());
    }

    public User getUser(int userId) {
        return userStorage.getUser(userId);
    }

    public List<User> getFriends(int userId) {
        return getFriendList(userStorage.getUser(userId).getFriends());
    }

    public List<User> getSameFriends(int userId, int otherId) {
        Set<Integer> sameFriends = new HashSet<>(userStorage.getUser(userId).getFriends());
        sameFriends.retainAll(userStorage.getUser(otherId).getFriends());
        return getFriendList(sameFriends);
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
                    + " userId = " + userId + ", friendsId = " + friendId);
        }

        // проверка на существование пользователей перед изменением сразу двух объектов
        userStorage.getUser(userId);
        userStorage.getUser(friendId);

        userStorage.getUser(userId).getFriends().add(friendId);
        userStorage.getUser(friendId).getFriends().add(userId);
        return userStorage.getUser(userId);
    }

    public User deleteUser(int userId) {
        return userStorage.deleteUser(userId);
    }

    public User deleteFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new IncorrectObjectModificationException("Невозможно удалить пользователя из друзей т.к. id совпадают"
                    + " userId = " + userId + ", friendsId = " + friendId);
        }

        // проверка на существование пользователей перед изменением сразу двух объектов
        userStorage.getUser(userId);
        userStorage.getUser(friendId);

        userStorage.getUser(userId).getFriends().remove(friendId);
        userStorage.getUser(friendId).getFriends().remove(userId);
        return userStorage.getUser(userId);
    }

    private List<User> getFriendList(Set<Integer> friendList) {
        List<User> friends = new ArrayList<>();
        for (int id : friendList) {
            friends.add(userStorage.getAllUsers().get(id));
        }
        return friends;
    }
}
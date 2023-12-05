package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.exceptions.IncorrectObjectModificationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enumerations.EventType;
import ru.yandex.practicum.filmorate.model.enumerations.Operation;
import ru.yandex.practicum.filmorate.storage.DBFilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.DBFriendsStorage;
import ru.yandex.practicum.filmorate.storage.DBLikesStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final DBFriendsStorage friendsStorage;
    private final DBLikesStorage likesStorage;
    private final FilmStorage filmStorage;
    private final MpaStorage mpaStorage;
    private final DBFilmGenreStorage filmGenreStorage;
    private final EventService eventService;

    @Autowired
    public UserService(@Qualifier("dBUserStorage") UserStorage userStorage,
                       @Qualifier("dBFilmStorage") FilmStorage filmStorage,
                       DBFriendsStorage friendsStorage,
                       DBLikesStorage likesStorage,
                       EventService eventService,
                       MpaStorage mpaStorage,
                       DBFilmGenreStorage filmGenreStorage) {
        this.userStorage = userStorage;
        this.friendsStorage = friendsStorage;
        this.likesStorage = likesStorage;
        this.filmStorage = filmStorage;
        this.mpaStorage = mpaStorage;
        this.filmGenreStorage = filmGenreStorage;
        this.eventService = eventService;
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

        eventService.crete(userId, friendId, EventType.FRIEND, Operation.ADD);

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

        eventService.crete(userId, friendId, EventType.FRIEND, Operation.REMOVE);

        return user;
    }

    public List<Film> getRecommendations(int userId) {
        // проверка на существование пользователя
        getUser(userId);

        Map<Integer, List<Integer>> allUsers = likesStorage.getUsersLikes();
        List<Integer> userList = allUsers.getOrDefault(userId, Collections.emptyList());

        if (userList.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Integer, Integer> recMap = allUsers.entrySet().stream()
                .filter(entry -> entry.getKey() != userId)
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    List<Integer> friendList = entry.getValue();
                    return (int) userList.stream().filter(friendList::contains).count();
                }));

        if (recMap.isEmpty()) {
            return Collections.emptyList();
        }

        int friendId = Collections.max(recMap.entrySet(), Map.Entry.comparingByValue()).getKey();
        List<Integer> friendList = allUsers.getOrDefault(friendId, Collections.emptyList());

        List<Film> recommendation = friendList.stream()
                .filter(id -> !userList.contains(id))
                .map(id -> {
                    Film film = filmStorage.getFilm(id);
                    film.setMpa(mpaStorage.getMpa(film.getMpa().getId()));
                    film.setGenres(filmGenreStorage.getFilmGenre(id));
                    film.setUserLikes(likesStorage.getLikes(id));
                    return film;
                })
                .collect(Collectors.toList());

        if (recommendation.isEmpty()) {
            return Collections.emptyList();
        }

        return recommendation;
    }

    public List<Event> getAllFeedByUserId(int userId) {
        userStorage.getUser(userId);
        return eventService.getAllByUserId(userId);
    }
}
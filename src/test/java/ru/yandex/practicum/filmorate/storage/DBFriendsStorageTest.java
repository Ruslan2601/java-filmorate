package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.DBUserStorage;

import java.time.LocalDate;

@JdbcTest
public class DBFriendsStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private final DBFriendsStorage friendsStorage;
    private final DBUserStorage userStorage;

    @Autowired
    public DBFriendsStorageTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.friendsStorage = new DBFriendsStorage(jdbcTemplate);
        this.userStorage = new DBUserStorage(jdbcTemplate);
    }

    @BeforeEach
    public void fillFriends() {
        jdbcTemplate.update("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;");
        fillUsers();

        String sqlQuery = "INSERT INTO friends (user_id, friend_id) " +
                "VALUES (?, ?);";
        for (int i = 1; i <= 5; ++i) {
            for (int j = 1; j <= 4; ++j) {
                if (i == j) {
                    continue;
                }
                jdbcTemplate.update(sqlQuery, i, j);
            }
        }
    }

    public User createUser(int id, String email, String login, String name, int year, int month, int day) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(LocalDate.of(year, month, day));
        return user;
    }

    public void fillUsers() {
        for (int i = 1; i <= 10; ++i) {
            userStorage.addUser(createUser(0, "e" + i + "@mail.ru", "login" + i,
                    "name " + i, 2000, 1, i));
        }
    }

    @Test
    public void getFriends() {
        Assertions.assertEquals(3, friendsStorage.getFriends(1).size(),
                "Все друзья указанного пользователя возвращаются неправильно");
        Assertions.assertTrue(friendsStorage.getFriends(1).contains(2),
                "Все друзья указанного пользователя возвращаются неправильно");
        Assertions.assertTrue(friendsStorage.getFriends(1).contains(3),
                "Все друзья указанного пользователя возвращаются неправильно");
        Assertions.assertTrue(friendsStorage.getFriends(1).contains(4),
                "Все друзья указанного пользователя возвращаются неправильно");
    }

    @Test
    public void addFriend() {
        friendsStorage.addFriend(1, 10);

        Assertions.assertTrue(friendsStorage.getFriends(1).contains(10),
                "Новый друг для указанного пользователя добавляется неправильно");
    }

    @Test
    public void deleteFriend() {
        friendsStorage.deleteFriend(1, 4);

        Assertions.assertEquals(2, friendsStorage.getFriends(1).size(),
                "Друг указанного пользователя удаляется непрвильно");
        Assertions.assertFalse(friendsStorage.getFriends(1).contains(4),
                "Друг указанного пользователя удаляется непрвильно");

        friendsStorage.deleteFriend(1, 3);

        Assertions.assertEquals(1, friendsStorage.getFriends(1).size(),
                "Друг указанного пользователя удаляется непрвильно");
        Assertions.assertFalse(friendsStorage.getFriends(1).contains(3),
                "Друг указанного пользователя удаляется непрвильно");
        Assertions.assertTrue(friendsStorage.getFriends(1).contains(2),
                "Друг указанного пользователя удаляется непрвильно");


    }

    @Test
    public void deleteFriends() {
        friendsStorage.deleteFriends(1);

        Assertions.assertEquals(0, friendsStorage.getFriends(1).size(),
                "Дрзья указанного пользователя удаляются непрвильно");
    }
}

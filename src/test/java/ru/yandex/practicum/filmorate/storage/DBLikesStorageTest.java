package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.DBFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.DBUserStorage;

import java.time.LocalDate;
import java.util.HashSet;

@JdbcTest
public class DBLikesStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private final DBLikesStorage likesStorage;
    private final DBFilmStorage filmStorage;
    private final DBUserStorage userStorage;

    @Autowired
    public DBLikesStorageTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.likesStorage = new DBLikesStorage(jdbcTemplate);
        this.filmStorage = new DBFilmStorage(jdbcTemplate);
        this.userStorage = new DBUserStorage(jdbcTemplate);
    }

    @BeforeEach
    public void fillFilmsAndUsers() {
        jdbcTemplate.update("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;");
        jdbcTemplate.execute("ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1;");
        fillUsers();
        fillFilms();

        String sqlQuery = "INSERT INTO likes (user_id, film_id) " +
                "VALUES (?, ?);";
        for (int i = 1; i <= 3; ++i) {
            for (int j = 1; j <= 4; ++j) {
                jdbcTemplate.update(sqlQuery, i, j);
            }
        }
    }

    public Film createFilm(int id, String name, String description, int year, int month, int day,
                           int duration, Mpa mpa) {
        Film film = new Film();
        film.setId(id);
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(LocalDate.of(year, month, day));
        film.setDuration(duration);
        film.setMpa(mpa);
        film.setGenres(new HashSet<>());
        film.setUserLikes(new HashSet<>());
        return film;
    }

    public void fillFilms() {
        for (int i = 1; i <= 5; ++i) {
            filmStorage.addFilm(createFilm(0, "name " + i, "description " + i, 2000, 1, i,
                    100 + i, new Mpa(1, "")));
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
        for (int i = 1; i <= 5; ++i) {
            userStorage.addUser(createUser(0, "e" + i + "@mail.ru", "login" + i,
                    "name " + i, 2000, 1, i));
        }
    }

    @Test
    public void getLikes() {
        Assertions.assertEquals(3, likesStorage.getLikes(1).size(),
                "Все лайки указанного фильма возвращаются неправильно");
        Assertions.assertTrue(likesStorage.getLikes(1).contains(1),
                "Все лайки указанного фильма возвращаются неправильно");
        Assertions.assertTrue(likesStorage.getLikes(1).contains(2),
                "Все лайки указанного фильма возвращаются неправильно");
        Assertions.assertTrue(likesStorage.getLikes(1).contains(3),
                "Все лайки указанного фильма возвращаются неправильно");
    }

    @Test
    public void addLike() {
        likesStorage.addLike(5, 1);

        Assertions.assertTrue(likesStorage.getLikes(1).contains(5),
                "Новый лайк для указанного фильма добавляется неправильно");
    }

    @Test
    public void deleteLike() {
        likesStorage.deleteLike(1, 1);

        Assertions.assertEquals(2, likesStorage.getLikes(1).size(),
                "Лайк указанного фильма удаляется неправильно");
        Assertions.assertFalse(likesStorage.getLikes(1).contains(1),
                "Лайк указанного фильма удаляется неправильно");

        likesStorage.deleteLike(2, 1);

        Assertions.assertEquals(1, likesStorage.getLikes(1).size(),
                "Лайк указанного фильма удаляется неправильно");
        Assertions.assertFalse(likesStorage.getLikes(1).contains(2),
                "Лайк указанного фильма удаляется неправильно");
        Assertions.assertTrue(likesStorage.getLikes(1).contains(3),
                "Лайк указанного фильма удаляется неправильно");
    }

    @Test
    public void deleteUserLikes() {
        likesStorage.deleteUserLikes(1);

        Assertions.assertEquals(2, likesStorage.getLikes(1).size(),
                "Все лайки указанного пользователя удаляются неправильно");
        Assertions.assertFalse(likesStorage.getLikes(1).contains(1),
                "Все лайки указанного пользователя удаляются неправильно");
    }

    @Test
    public void deleteFilmLikes() {
        likesStorage.deleteFilmLikes(1);

        Assertions.assertEquals(0, likesStorage.getLikes(1).size(),
                "Все лайки указанного фильма удаляются неправильно");
    }
}

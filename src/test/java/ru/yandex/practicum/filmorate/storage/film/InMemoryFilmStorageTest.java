package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.exceptions.AddExistObjectException;
import ru.yandex.practicum.filmorate.exception.exceptions.UpdateNonExistObjectException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class InMemoryFilmStorageTest {

    public FilmStorage filmStorage;

    @BeforeEach
    public void setFilmStorage() {
        this.filmStorage = new InMemoryFilmStorage();
    }

    public Film createFilm(String name, String description, int year, int month, int day, int duration) {
        Film film = new Film();
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(LocalDate.of(year, month, day));
        film.setDuration(duration);
        return film;
    }

    public Film createFilm(int id, String name, String description, int year, int month, int day, int duration) {
        Film film = createFilm(name, description, year, month, day, duration);
        film.setId(id);
        return film;
    }

    public Film createFilm(int id, String name, String description, int year, int month, int day, int duration
            , Set<Integer> userLikes) {
        Film film = createFilm(id, name, description, year, month, day, duration);
        film.setUserLikes(userLikes);
        return film;
    }

    public void fillFilms() {
        for (int i = 1; i <= 20; ++i) {
            filmStorage.addFilm(createFilm(i + " film", i + "", 2000, 1, i, 100 + i));
        }
    }

    @Test
    public void getAllFilmsEmpty() {
        Assertions.assertEquals(new HashMap<>(), filmStorage.getAllFilms()
                , "Возвращается непустой список фильмов");
    }

    @Test
    public void getAllFilmsFill() {
        fillFilms();

        Assertions.assertEquals(20, filmStorage.getAllFilms().size()
                , "Возвращается неправильное количество фильмов");
    }

    @Test
    public void getExistFilm() {
        filmStorage.addFilm(createFilm("1", "1", 2000, 1, 1, 1));

        Assertions.assertEquals(createFilm(1, "1", "1", 2000, 1, 1, 1
                        , new HashSet<>())
                , filmStorage.getFilm(1)
                , "Фильм с указанным id невозможно получить");
    }

    @Test
    public void getNonExistFilm() {
        Assertions.assertThrows(UpdateNonExistObjectException.class
                , () -> filmStorage.getFilm(1)
                , "При добавлении фильма ожидалось UpdateNonExistObjectException");
    }


    @Test
    public void addFirstFilm() {
        filmStorage.addFilm(createFilm("1", "1", 2000, 1, 1, 1));

        Assertions.assertEquals(createFilm(1, "1", "1", 2000, 1, 1, 1
                        , new HashSet<>())
                , filmStorage.getFilm(1)
                , "Первый созданный фильм неправильно добавляется");
    }

    @Test
    public void addNewFilm() {
        fillFilms();
        filmStorage.addFilm(createFilm("21", "21", 2000, 1, 21, 21));

        Assertions.assertEquals(createFilm(21, "21", "21", 2000, 1, 21, 21
                        , new HashSet<>())
                , filmStorage.getFilm(21)
                , "Новый фильм неправильно добавляется");
    }

    @Test
    public void addExistFilm() {
        fillFilms();

        Assertions.assertThrows(AddExistObjectException.class
                , () -> filmStorage.addFilm(createFilm(1, "1", "1"
                        , 2000, 1, 1, 1))
                , "при добавлнении фильма ожидалось AddExistObjectException");
    }

    @Test
    public void addFilmWithoutOptionalParameters() {
        filmStorage.addFilm(createFilm("1", null, 2000, 1, 1, 1));

        Assertions.assertEquals(createFilm(1, "1", "", 2000, 1, 1, 1
                        , new HashSet<>())
                , filmStorage.getFilm(1)
                , "Необазятельные параметры фильма неправильно обрабатываются");
    }


    @Test
    public void updateExistFilm() {
        filmStorage.addFilm(createFilm("1", "1", 2000, 1, 1, 1));

        Assertions.assertEquals(createFilm(1, "2", "2", 2000, 2, 2, 2
                        , new HashSet<>())
                , filmStorage.updateFilm(createFilm(1, "2", "2", 2000, 2, 2, 2))
                , "Фильм неправильно обновляется");
    }

    @Test
    public void updateNonExistFilm() {
        Assertions.assertThrows(UpdateNonExistObjectException.class
                , () -> filmStorage.updateFilm(createFilm("1", "1", 2000, 1, 1, 1))
                , "При обновлении фильма ожидалось UpdateNonExistObjectException");
    }

    @Test
    public void deleteExistFilm() {
        filmStorage.addFilm(createFilm("1", "1", 2000, 1, 1, 1));

        Assertions.assertEquals(createFilm(1, "1", "1", 2000, 1, 1, 1
                        , new HashSet<>())
                , filmStorage.deleteFilm(1)
                , "Фильм неправильно обновляется");
    }

    @Test
    public void deleteNonExistFilm() {
        Assertions.assertThrows(UpdateNonExistObjectException.class
                , () -> filmStorage.deleteFilm(1)
                , "При удалении фильма ожидалось UpdateNonExistObjectException");
    }
}

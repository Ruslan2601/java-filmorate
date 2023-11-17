package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.exceptions.AddExistObjectException;
import ru.yandex.practicum.filmorate.exception.exceptions.IncorrectObjectModificationException;
import ru.yandex.practicum.filmorate.exception.exceptions.UpdateNonExistObjectException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilmServiceTest {
    public FilmService filmService;

    @BeforeEach
    public void setFilmService() {
        filmService = new FilmService(new InMemoryFilmStorage());
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

    public Film createFilm(int id, String name, String description, int year, int month, int day, int duration,
                           Set<Integer> userLikes) {
        Film film = createFilm(id, name, description, year, month, day, duration);
        film.setUserLikes(userLikes);
        return film;
    }

    public void fillFilms() {
        for (int i = 1; i <= 20; ++i) {
            filmService.addFilm(createFilm(i + " film", i + "", 2000, 1, i, 100 + i));
        }
    }

    @Test
    public void getAllFilmsEmpty() {
        Assertions.assertEquals(new ArrayList<>(), filmService.getAllFilms(),
                "Неправильно возвращается пустой список фильмов");
    }

    @Test
    public void getAllFilmsFill() {
        fillFilms();

        Assertions.assertEquals(20, filmService.getAllFilms().size(),
                "Неправильно возвращается заполненный список фильмов");
    }

    @Test
    public void getExistFilm() {
        filmService.addFilm(createFilm("1", "1", 2000, 1, 1, 1));

        Assertions.assertEquals(createFilm(1, "1", "1", 2000, 1, 1, 1,
                        new HashSet<>()),
                filmService.getFilm(1),
                "Новый фильм неправильно добавляется");
    }

    @Test
    public void getNonExistFilm() {
        Assertions.assertThrows(UpdateNonExistObjectException.class,
                () -> filmService.getFilm(1),
                "При добавлении фильма ожидалось UpdateNonExistObjectException");
    }

    @Test
    public void getEmptyMostLikedFilms() {
        Assertions.assertEquals(new ArrayList<>(), filmService.getMostLikedFilms(10),
                "Неправильно возвращаеться пустой список лучших фильмов");
    }

    @Test
    public void getFillMostLikedFilms() {
        fillFilms();

        for (int i = 0; i < filmService.getAllFilms().size() / 2; ++i) {
            Set<Integer> userLikes = new HashSet<>();
            for (int j = 1; j <= i; ++j) {
                userLikes.add(j);
            }
            filmService.getAllFilms().get(i).setUserLikes(userLikes);
        }
        filmService.getAllFilms().get(0).setUserLikes(Set.of(5, 10));
        filmService.getAllFilms().get(5).setUserLikes(Set.of(1, 16, 100));
        filmService.getAllFilms().get(15).setUserLikes(Set.of(1, 8));

        List<Film> mostLikedFilms = filmService.getMostLikedFilms(10);

        Assertions.assertEquals(10, mostLikedFilms.get(0).getId(),
                "Неправильно возвращаеться список лучших фильмов");
        Assertions.assertEquals(9, mostLikedFilms.get(1).getId(),
                "Неправильно возвращаеться список лучших фильмов");
        Assertions.assertEquals(8, mostLikedFilms.get(2).getId(),
                "Неправильно возвращаеться список лучших фильмов");
        Assertions.assertEquals(7, mostLikedFilms.get(3).getId(),
                "Неправильно возвращаеться список лучших фильмов");
        Assertions.assertEquals(5, mostLikedFilms.get(4).getId(),
                "Неправильно возвращаеться список лучших фильмов");
        Assertions.assertEquals(4, mostLikedFilms.get(5).getId(),
                "Неправильно возвращаеться список лучших фильмов");
        Assertions.assertEquals(6, mostLikedFilms.get(6).getId(),
                "Неправильно возвращаеться список лучших фильмов");
        Assertions.assertEquals(1, mostLikedFilms.get(7).getId(),
                "Неправильно возвращаеться список лучших фильмов");
        Assertions.assertEquals(3, mostLikedFilms.get(8).getId(),
                "Неправильно возвращаеться список лучших фильмов");
        Assertions.assertEquals(16, mostLikedFilms.get(9).getId(),
                "Неправильно возвращаеться список лучших фильмов");
    }

    @Test
    public void getOneMostLikedFilms() {
        fillFilms();

        Assertions.assertEquals(1, filmService.getMostLikedFilms(1).size(),
                "Неправильно возвращаеться список лучших фильмов при значинии в 1 фильм");
    }

    @Test
    public void getFiveMostLikedFilms() {
        fillFilms();

        Assertions.assertEquals(5, filmService.getMostLikedFilms(5).size(),
                "Неправильно возвращаеться список лучших фильмов при значинии в 5 фильмов");
    }

    @Test
    public void addFirstFilm() {
        filmService.addFilm(createFilm("1", "1", 2000, 1, 1, 1));

        Assertions.assertEquals(createFilm(1, "1", "1", 2000, 1, 1, 1,
                        new HashSet<>()),
                filmService.getFilm(1),
                "Первый созданный фильм неправильно добавляется");
    }

    @Test
    public void addNewFilm() {
        fillFilms();
        filmService.addFilm(createFilm("21", "21", 2000, 1, 21, 21));

        Assertions.assertEquals(createFilm(21, "21", "21", 2000, 1, 21, 21,
                        new HashSet<>()),
                filmService.getFilm(21),
                "Новый фильм неправильно добавляется");
    }

    @Test
    public void addExistFilm() {
        fillFilms();

        Assertions.assertThrows(AddExistObjectException.class,
                () -> filmService.addFilm(createFilm(1, "1", "1",
                        2000, 1, 1, 1)),
                "при добавлнении фильма ожидалось AddExistObjectException");
    }

    @Test
    public void addFilmWithoutOptionalParameters() {
        filmService.addFilm(createFilm("1", null, 2000, 1, 1, 1));

        Assertions.assertEquals(createFilm(1, "1", "", 2000, 1, 1, 1,
                        new HashSet<>()),
                filmService.getFilm(1),
                "Необазятельные параметры фильма неправильно обрабатываются");
    }


    @Test
    public void updateExistFilm() {
        filmService.addFilm(createFilm("1", "1", 2000, 1, 1, 1));

        Assertions.assertEquals(createFilm(1, "2", "2", 2000, 2, 2, 2,
                        new HashSet<>()),
                filmService.updateFilm(createFilm(1, "2", "2", 2000, 2, 2, 2)),
                "Фильм неправильно обновляется");
    }

    @Test
    public void updateNonExistFilm() {
        Assertions.assertThrows(UpdateNonExistObjectException.class,
                () -> filmService.updateFilm(createFilm("1", "1", 2000, 1, 1, 1)),
                "При обновлении фильма ожидалось UpdateNonExistObjectException");
    }

    @Test
    public void addLikeToFilm() {
        filmService.addFilm(createFilm("1", "1", 2000, 1, 1, 1));

        Assertions.assertTrue(filmService.addLikeToFilm(1, 1).getUserLikes().contains(1),
                "Неправильное добавление лайка к фильму");
    }

    @Test
    public void addExistLikeToFilm() {
        filmService.addFilm(createFilm("1", "1", 2000, 1, 1, 1));
        filmService.addLikeToFilm(1, 1);

        Assertions.assertThrows(IncorrectObjectModificationException.class,
                () -> filmService.addLikeToFilm(1, 1),
                "При добавлении лайка к фильму ожидалось IncorrectObjectModificationException");
    }

    @Test
    public void addLikeToNonExistFilm() {
        Assertions.assertThrows(UpdateNonExistObjectException.class,
                () -> filmService.addLikeToFilm(1, 1),
                "При добавлении лайка к фильму ожидалось UpdateNonExistObjectException");
    }

    @Test
    public void deleteExistFilm() {
        filmService.addFilm(createFilm("1", "1", 2000, 1, 1, 1));

        Assertions.assertEquals(createFilm(1, "1", "1", 2000, 1, 1, 1,
                        new HashSet<>()),
                filmService.deleteFilm(1),
                "Фильм неправильно обновляется");
    }

    @Test
    public void deleteNonExistFilm() {
        Assertions.assertThrows(UpdateNonExistObjectException.class,
                () -> filmService.deleteFilm(1),
                "При удалении фильма ожидалось UpdateNonExistObjectException");
    }

    @Test
    public void deleteLikeToFilm() {
        filmService.addFilm(createFilm("1", "1", 2000, 1, 1, 1));
        filmService.addLikeToFilm(1, 1);

        Assertions.assertFalse(filmService.deleteLikeToFilm(1, 1).getUserLikes().contains(1),
                "Неправильное удаление лайка у фильма");
    }

    @Test
    public void deleteNonExistLikeToFilm() {
        filmService.addFilm(createFilm("1", "1", 2000, 1, 1, 1));

        Assertions.assertThrows(UpdateNonExistObjectException.class,
                () -> filmService.deleteLikeToFilm(1, 1),
                "При удалении лайка у фильма ожидалось UpdateNonExistObjectException");
    }

    @Test
    public void deleteLikeToNonExistFilm() {
        Assertions.assertThrows(UpdateNonExistObjectException.class,
                () -> filmService.addLikeToFilm(1, 1),
                "При удалении лайка у фильма ожидалось UpdateNonExistObjectException");
    }
}

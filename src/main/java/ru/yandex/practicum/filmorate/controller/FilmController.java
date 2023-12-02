package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        log.info("Получен GET запрос на получение всех фильмов");
        return ResponseEntity.ok(filmService.getAllFilms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilm(@PathVariable("id") int filmId) {
        log.info("Получен GET запрос на получение фильма по id");
        return ResponseEntity.ok(filmService.getFilm(filmId));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getMostLikedFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Получен GET запрос на получение лучших фильмов");
        return ResponseEntity.ok(filmService.getMostLikedFilms(count));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Film>> getSearch(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "by", required = false) String by
    ) {
        log.debug("Возвращаем фильмы с поисковым запросом: {}", query);
        if (query == null || by == null) {
            return ResponseEntity.ok(filmService.getMostLikedFilms(filmService.getAllFilms().size()));
        } else {
            return ResponseEntity.ok(filmService.searchFilms(query, by));
        }
    }

    @PostMapping
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        log.info("Получен POST запрос на добавление нового фильма");
        return ResponseEntity.ok(filmService.addFilm(film));
    }

    @PutMapping()
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен PUT запрос на обновление фильма");
        return ResponseEntity.ok(filmService.updateFilm(film));
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> addLikeToFilm(@PathVariable("id") int filmId, @PathVariable int userId) {
        log.info("Получен PUT запрос на добавление лайка фильму");
        return ResponseEntity.ok(filmService.addLikeToFilm(filmId, userId));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> deleteLikeToFilm(@PathVariable("id") int filmId, @PathVariable int userId) {
        log.info("Получен DELETE запрос на удаление лайка фильму");
        return ResponseEntity.ok(filmService.deleteLikeToFilm(filmId, userId));
    }

    @GetMapping("/common")
    public ResponseEntity<List<Film>> getSameFilms(@RequestParam int userId, @RequestParam int friendId) {
        log.info("Получен GET запрос на получение общих фильмов с другом");
        return ResponseEntity.ok(filmService.getCommonFriendFilms(userId, friendId));
    }
}
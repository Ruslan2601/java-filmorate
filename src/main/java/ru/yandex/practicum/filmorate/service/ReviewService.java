package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.exceptions.AddExistObjectException;
import ru.yandex.practicum.filmorate.exception.exceptions.UpdateNonExistObjectException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.DBReviewUserLikesStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.DBReviewStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final DBReviewUserLikesStorage reviewUserLikesStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public ReviewService(@Qualifier("dBReviewStorage") DBReviewStorage reviewStorage,
                         DBReviewUserLikesStorage reviewUserLikesStorage,
                         @Qualifier("dBUserStorage") UserStorage userStorage,
                         @Qualifier("dBFilmStorage") FilmStorage filmStorage) {
        this.reviewStorage = reviewStorage;
        this.reviewUserLikesStorage = reviewUserLikesStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public List<Review> getReviews(int filmId, int count) {
        return reviewStorage.getReviews(filmId, count);
    }

    public Review getReview(int reviewId) {
        return reviewStorage.getReview(reviewId);
    }

    public Review addReview(Review review) {
        userStorage.getUser(review.getUserId());
        filmStorage.getFilm(review.getFilmId());
        return reviewStorage.addReview(review);
    }

    public Review updateReview(Review review) {
        userStorage.getUser(review.getUserId());
        filmStorage.getFilm(review.getFilmId());
        return reviewStorage.updateReview(review);
    }

    public Review addLikeToReview(int reviewId, int userId) {
        Review review = reviewStorage.getReview(reviewId);
        userStorage.getUser(userId);

        if (reviewUserLikesStorage.addLikeToReview(reviewId, userId)) {
            review.setUseful(review.getUseful() + 1);
            return review;
        } else {
            throw new AddExistObjectException("Like от этого пользователя уже существует " +
                    "reviewId = " + reviewId + ", userId = " + userId);
        }
    }

    public Review addDislikeToReview(int reviewId, int userId) {
        Review review = reviewStorage.getReview(reviewId);
        userStorage.getUser(userId);

        if (reviewUserLikesStorage.addDislikeToReview(reviewId, userId)) {
            review.setUseful(review.getUseful() - 1);
            return review;
        } else {
            throw new AddExistObjectException("Dislike от этого пользователя уже существует " +
                    "reviewId = " + reviewId + ", userId = " + userId);
        }
    }

    public Review deleteReview(int reviewId) {
        Review review = reviewStorage.getReview(reviewId);
        reviewUserLikesStorage.deleteAllReactionsFromReview(reviewId);
        reviewStorage.deleteReview(reviewId);
        return review;
    }

    public Review deleteLikeToReview(int reviewId, int userId) {
        Review review = reviewStorage.getReview(reviewId);
        userStorage.getUser(userId);

        if (reviewUserLikesStorage.deleteReactionFromReview(reviewId, userId)) {
            review.setUseful(review.getUseful() - 1);
            return review;
        } else {
            throw new UpdateNonExistObjectException("Like от этого пользователя не существует " +
                    "reviewId = " + reviewId + ", userId = " + userId);
        }
    }

    public Review deleteDislikeToReview(int reviewId, int userId) {
        Review review = reviewStorage.getReview(reviewId);
        userStorage.getUser(userId);

        if (reviewUserLikesStorage.deleteReactionFromReview(reviewId, userId)) {
            review.setUseful(review.getUseful() + 1);
            return review;
        } else {
            throw new UpdateNonExistObjectException("Dislike от этого пользователя не существует " +
                    "reviewId = " + reviewId + ", userId = " + userId);
        }
    }
}

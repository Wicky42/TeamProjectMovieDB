package org.example.backend.domain;

public record Movie(
        String title,
        String poster,
        String year,
        String type,
        String imdbID,
        String genre,
        String metascore,
        String imdbRating
) {
}

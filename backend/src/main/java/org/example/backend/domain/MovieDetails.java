package org.example.backend.domain;

public record MovieDetails(
        String title,
        String poster,
        int year,
        String type,
        String imdbID,
        String genre,
        String metascore,
        String imdbRating,
        String plot
) {
}

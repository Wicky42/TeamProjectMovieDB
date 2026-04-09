package org.example.backend.domain;

import org.springframework.data.annotation.Id;

public record Movie(
        String title,
        String poster,
        int year,
        String type,
        String imdbID,
        String genre,
        String metascore,
        String imdbRating
) {
}

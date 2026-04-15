package org.example.backend.domain;

public record Movie(
        String title,
        String year,
        String imdbID,
        String type,
        String poster
) {
}

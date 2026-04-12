package org.example.backend.domain;

public record Movie(
        String title,
        int year,
        String imdbID,
        String type,
        String poster
) {
}

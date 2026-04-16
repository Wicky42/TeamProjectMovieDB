package org.example.backend.dto;

public record MovieResponseDto(
        String title,
        String poster,
        String year,
        String type,
        String imdbID,
        String genre,
        String metascore,
        String imdbRating,
        String plot
) {
}

package org.example.backend.domain;

import org.springframework.data.annotation.Id;

import lombok.With;

import java.util.List;

@With
public record Watchlist(
        @Id
        String id,
        String name,
        List<MovieDetails> movieDetails,
        String description
) {
}

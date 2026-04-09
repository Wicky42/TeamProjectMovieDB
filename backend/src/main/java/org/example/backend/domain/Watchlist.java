package org.example.backend.domain;

import org.springframework.data.annotation.Id;

import java.util.List;

public record Watchlist(
        @Id
        String id,
        String name,
        List<Movie> movies,
        String description
) {
}

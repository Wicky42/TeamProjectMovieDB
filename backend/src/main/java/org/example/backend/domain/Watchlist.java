package org.example.backend.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.With;

import java.util.List;

@Document("watchlists")
@With
public record Watchlist(
        @Id
        String id,
        String name,
        List<String> watchlistEntryIds,
        String description
) {
}

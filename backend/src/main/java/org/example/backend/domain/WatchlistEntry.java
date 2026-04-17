package org.example.backend.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.With;

@Document("watchlist_entries")
@With
public record WatchlistEntry(
  @Id
  String id,
  String imdbId,
  String userRating,
  boolean watched
) {
}

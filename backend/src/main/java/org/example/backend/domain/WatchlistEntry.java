package org.example.backend.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("watchlist_entries")
public record WatchlistEntry(
  @Id
  String id,
  String imdbId,
  String userRating,
  boolean watched
) {
}

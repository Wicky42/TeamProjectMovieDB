package org.example.backend.domain;

import org.springframework.data.annotation.Id;

public record WatchlistEntry(
  @Id
  String id,
  String imdbId,
  String userRating,
  boolean watched
) {
}

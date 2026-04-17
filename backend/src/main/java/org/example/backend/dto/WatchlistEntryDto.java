package org.example.backend.dto;

import lombok.With;

@With
public record WatchlistEntryDto(
  String id,
  String imdbId,
  String userRating,
  boolean watched,
  String title,
  String poster,
  String year,
  String type,
  String genre,
  String metascore,
  String imdbRating,
  String plot
) {

}

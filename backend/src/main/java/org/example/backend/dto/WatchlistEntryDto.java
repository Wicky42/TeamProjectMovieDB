package org.example.backend.dto;

public record WatchlistEntryDto(
  String id,
  String imdbID,
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

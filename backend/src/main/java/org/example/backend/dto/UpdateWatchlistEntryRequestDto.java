package org.example.backend.dto;

public record UpdateWatchlistEntryRequestDto(
  String userRating,
  Boolean watched
) {

}

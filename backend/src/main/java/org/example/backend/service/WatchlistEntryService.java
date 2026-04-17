package org.example.backend.service;

import org.example.backend.domain.WatchlistEntry;
import org.example.backend.dto.MovieResponseDto;
import org.example.backend.dto.UpdateWatchlistEntryRequestDto;
import org.example.backend.dto.WatchlistEntryDto;
import org.example.backend.exception.WatchlistEntryNotFoundException;
import org.example.backend.repo.WatchlistEntryRepo;
import org.springframework.stereotype.Service;

@Service
public class WatchlistEntryService {
  private final IdService idService;
  private final MovieService movieService;
  private final WatchlistEntryRepo watchlistEntryRepo;

  public WatchlistEntryService(IdService idService, MovieService movieService, WatchlistEntryRepo watchlistEntryRepo) {
    this.idService = idService;
    this.movieService = movieService;
    this.watchlistEntryRepo = watchlistEntryRepo;
  }

  public WatchlistEntry getOrCreateWatchlistEntry(String imdbID) {
    WatchlistEntry entry = watchlistEntryRepo.findByImdbID(imdbID)
      .orElseGet(() -> watchlistEntryRepo.save(
        new WatchlistEntry(
          idService.generateWatchlistEntryId(),
          imdbID,
          "",
          false
        )
      ));

    return entry;
  }

  public WatchlistEntryDto toWatchlistEntryDto(WatchlistEntry entry) {
    MovieResponseDto movieDto = movieService.createMovieResponseDtoFromImdbId(entry.imdbID());

    return new WatchlistEntryDto(
      entry.id(),
      entry.imdbID(),
      entry.userRating(),
      entry.watched(),
      movieDto.title(),
      movieDto.poster(),
      movieDto.year(),
      movieDto.type(),
      movieDto.genre(),
      movieDto.metascore(),
      movieDto.imdbRating(),
      movieDto.plot()
    );
  }

  public WatchlistEntryDto updateEntry(String entryId, UpdateWatchlistEntryRequestDto requestData) {
    WatchlistEntry entry = watchlistEntryRepo.findById(entryId)
      .orElseThrow(() -> new WatchlistEntryNotFoundException(entryId));

    WatchlistEntry updated = new WatchlistEntry(
      entry.id(),
      entry.imdbID(),
      requestData.userRating() != null ? requestData.userRating() : entry.userRating(),
      requestData.watched() != null ? requestData.watched() : entry.watched()
    );

    watchlistEntryRepo.save(updated);
    return toWatchlistEntryDto(updated);
  }
}

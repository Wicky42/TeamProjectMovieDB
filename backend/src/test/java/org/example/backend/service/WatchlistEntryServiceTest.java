package org.example.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.example.backend.domain.WatchlistEntry;
import org.example.backend.dto.MovieResponseDto;
import org.example.backend.dto.WatchlistEntryDto;
import org.example.backend.repo.WatchlistEntryRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WatchlistEntryServiceTest {

  @Mock IdService idService;
  @Mock MovieService movieService;
  @Mock WatchlistEntryRepo watchlistEntryRepo;

  @InjectMocks WatchlistEntryService watchlistEntryService;

  private WatchlistEntry validWatchlistEntry() {
    return new WatchlistEntry(
      "WE-1",
      "tt1375666",
      "8",
      true
    );
  }

  private MovieResponseDto validMovieResponseDto() {
    return new MovieResponseDto(
      "Inception",
      "poster-url",
      "2010",
      "movie",
      "tt1375666",
      "Sci-Fi",
      "74",
      "8.8",
      "A thief who steals corporate secrets through dream-sharing technology is given the inverse task of planting an idea."
    );
  }

  @Test
  void getOrCreateWatchlistEntry_returnsExistingEntry_whenEntryAlreadyExists() {
    WatchlistEntry entry = validWatchlistEntry();
    when(watchlistEntryRepo.findByImdbID(entry.imdbID())).thenReturn(Optional.of(entry));

    assertEquals(entry, watchlistEntryService.getOrCreateWatchlistEntry(entry.imdbID()));
    verify(watchlistEntryRepo).findByImdbID(entry.imdbID());
    verifyNoMoreInteractions(idService, movieService, watchlistEntryRepo);
  }

  @Test
  void getOrCreateWatchlistEntry_createsAndReturnsEntry_whenEntryDoesNotExist() {
    String imdbId = "tt1375666";
    WatchlistEntry newEntry = new WatchlistEntry(
      "WE-1",
      imdbId,
      "",
      false
    );

    when(watchlistEntryRepo.findByImdbID(imdbId)).thenReturn(Optional.empty());
    when(idService.generateWatchlistEntryId()).thenReturn("WE-1");
    when(watchlistEntryRepo.save(newEntry)).thenReturn(newEntry);

    assertEquals(newEntry, watchlistEntryService.getOrCreateWatchlistEntry(imdbId));
    verify(watchlistEntryRepo).findByImdbID(imdbId);
    verify(idService).generateWatchlistEntryId();
    verify(watchlistEntryRepo).save(newEntry);
    verifyNoMoreInteractions(idService, movieService, watchlistEntryRepo);
  }

  @Test
  void toWatchlistEntryDto_returnsMappedDto_whenCalled() {
    WatchlistEntry entry = validWatchlistEntry();
    MovieResponseDto movieDto = validMovieResponseDto();

    when(movieService.createMovieResponseDtoFromImdbId(entry.imdbID())).thenReturn(movieDto);

    WatchlistEntryDto expected = new WatchlistEntryDto(
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

    assertEquals(expected, watchlistEntryService.toWatchlistEntryDto(entry));
    verify(movieService).createMovieResponseDtoFromImdbId(entry.imdbID());
    verifyNoMoreInteractions(idService, movieService, watchlistEntryRepo);
  }
}
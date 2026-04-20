package org.example.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.example.backend.domain.WatchlistEntry;
import org.example.backend.dto.MovieResponseDto;
import org.example.backend.dto.UpdateWatchlistEntryRequestDto;
import org.example.backend.dto.WatchlistEntryDto;
import org.example.backend.exception.WatchlistEntryNotFoundException;
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

  private WatchlistEntryDto validWatchlistEntryDto() {
    return new WatchlistEntryDto(
            "WE-1",
            "tt1375666",
            "8",
            true,
            "Inception",
            "poster-url",
            "2010",
            "movie",
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

  @Test
  void updateEntry_updatesUserRatingAndWatchedSuccessfully() {
    WatchlistEntry existing = new WatchlistEntry(
            "WE-1",
            "tt1375666",
            "7",
            false
    );

    MovieResponseDto movieDto = validMovieResponseDto();
    when(movieService.createMovieResponseDtoFromImdbId(existing.imdbID())).thenReturn(movieDto);

    UpdateWatchlistEntryRequestDto request =
            new UpdateWatchlistEntryRequestDto("9", true);

    WatchlistEntry expected = existing
            .withUserRating(request.userRating())
            .withWatched(request.watched());

    when(watchlistEntryRepo.findById("WE-1")).thenReturn(Optional.of(existing));

    WatchlistEntryDto result = watchlistEntryService.updateEntry("WE-1", request);

    verify(watchlistEntryRepo).findById("WE-1");
    verify(watchlistEntryRepo).save(expected);
    verifyNoMoreInteractions(watchlistEntryRepo);

    assertEquals(expected.id(), result.id());
    assertEquals(expected.imdbID(), result.imdbID());
    assertEquals(expected.userRating(), result.userRating());
    assertEquals(expected.watched(), result.watched());
  }

  @Test
  void updateEntry_keepsUserRating_whenUserRatingIsNull() {
    WatchlistEntry existing = new WatchlistEntry(
            "WE-1",
            "tt1375666",
            "7",
            false
    );

    MovieResponseDto movieDto = validMovieResponseDto();
    when(movieService.createMovieResponseDtoFromImdbId(existing.imdbID())).thenReturn(movieDto);

    UpdateWatchlistEntryRequestDto request =
            new UpdateWatchlistEntryRequestDto(null, true);

    WatchlistEntry expected = existing
            .withWatched(request.watched());

    when(watchlistEntryRepo.findById("WE-1")).thenReturn(Optional.of(existing));

    WatchlistEntryDto result = watchlistEntryService.updateEntry("WE-1", request);

    verify(watchlistEntryRepo).findById("WE-1");
    verify(watchlistEntryRepo).save(expected);
    verifyNoMoreInteractions(watchlistEntryRepo);

    assertEquals(expected.id(), result.id());
    assertEquals(expected.imdbID(), result.imdbID());
    assertEquals(expected.userRating(), result.userRating());
    assertEquals(expected.watched(), result.watched());
  }

  @Test
  void updateEntry_keepsWatched_whenWatchedIsNull() {
    WatchlistEntry existing = new WatchlistEntry(
            "WE-1",
            "tt1375666",
            "7",
            false
    );

    MovieResponseDto movieDto = validMovieResponseDto();
    when(movieService.createMovieResponseDtoFromImdbId(existing.imdbID())).thenReturn(movieDto);

    UpdateWatchlistEntryRequestDto request =
            new UpdateWatchlistEntryRequestDto("8", null);

    WatchlistEntry expected = existing
            .withUserRating(request.userRating());

    when(watchlistEntryRepo.findById("WE-1")).thenReturn(Optional.of(existing));

    WatchlistEntryDto result = watchlistEntryService.updateEntry("WE-1", request);

    verify(watchlistEntryRepo).findById("WE-1");
    verify(watchlistEntryRepo).save(expected);
    verifyNoMoreInteractions(watchlistEntryRepo);

    assertEquals(expected.id(), result.id());
    assertEquals(expected.imdbID(), result.imdbID());
    assertEquals(expected.userRating(), result.userRating());
    assertEquals(expected.watched(), result.watched());
  }

  @Test
  void updateEntry_throwsWatchlistEntryNotFoundException_whenEntryDoesNotExist() {
    when(watchlistEntryRepo.findById("WE-404")).thenReturn(Optional.empty());

    assertThrows(
            WatchlistEntryNotFoundException.class,
            () -> watchlistEntryService.updateEntry(
                    "WE-404",
                    new UpdateWatchlistEntryRequestDto("9", true)
            )
    );

    verify(watchlistEntryRepo).findById("WE-404");
    verifyNoMoreInteractions(watchlistEntryRepo);
  }

  @Test
  void createWatchListEntryDtoList_returnsMappedDtos_whenAllIdsExist() {
    WatchlistEntry e1 = validWatchlistEntry().withId("WE-1");
    WatchlistEntry e2 = validWatchlistEntry().withId("WE-2");

    WatchlistEntryDto d1 = validWatchlistEntryDto().withId("WE-1");
    WatchlistEntryDto d2 = validWatchlistEntryDto().withId("WE-2");

    when(movieService.createMovieResponseDtoFromImdbId(e1.imdbID()))
            .thenReturn(validMovieResponseDto());
    when(movieService.createMovieResponseDtoFromImdbId(e2.imdbID()))
            .thenReturn(validMovieResponseDto());
    when(watchlistEntryRepo.findById("WE-1")).thenReturn(Optional.of(e1));
    when(watchlistEntryRepo.findById("WE-2")).thenReturn(Optional.of(e2));

    List<WatchlistEntryDto> result =
            watchlistEntryService.createWatchListEntryDtoList(List.of("WE-1", "WE-2"));

    assertEquals(List.of(d1, d2), result);

    verify(watchlistEntryRepo).findById("WE-1");
    verify(watchlistEntryRepo).findById("WE-2");
    verifyNoMoreInteractions(watchlistEntryRepo);
  }

  @Test
  void createWatchListEntryDtoList_throws_whenAnyIdDoesNotExist() {
    WatchlistEntry e1 = validWatchlistEntry().withId("WE-1");

    when(movieService.createMovieResponseDtoFromImdbId(e1.imdbID()))
            .thenReturn(validMovieResponseDto());
    when(watchlistEntryRepo.findById("WE-1")).thenReturn(Optional.of(e1));
    when(watchlistEntryRepo.findById("WE-2")).thenReturn(Optional.empty());

    WatchlistEntryNotFoundException ex = assertThrows(
            WatchlistEntryNotFoundException.class,
            () -> watchlistEntryService.createWatchListEntryDtoList(List.of("WE-1", "WE-2"))
    );

    assertEquals("WatchlistEntry with id WE-2 not found.", ex.getMessage());
    verify(watchlistEntryRepo).findById("WE-1");
    verify(watchlistEntryRepo).findById("WE-2");
    verifyNoMoreInteractions(watchlistEntryRepo);
  }

  @Test
  void createWatchListEntryDtoList_returnsEmptyList_whenInputIsEmpty() {
    List<WatchlistEntryDto> result =
            watchlistEntryService.createWatchListEntryDtoList(List.of());

    assertEquals(List.of(), result);
    verifyNoInteractions(watchlistEntryRepo);
  }

  @Test
  void findEntries_returnsAllEntries_whenWatchedIsNull() {
    WatchlistEntry we1 = validWatchlistEntry()
            .withId("WE-1")
            .withWatched(true);
    WatchlistEntry we2 = validWatchlistEntry()
            .withId("WE-2")
            .withImdbID("other-id")
            .withWatched(false);

    MovieResponseDto m1 = validMovieResponseDto();
    MovieResponseDto m2 = validMovieResponseDto();

    WatchlistEntryDto weDto1 = validWatchlistEntryDto()
            .withId(we1.id())
            .withWatched(we1.watched());

    WatchlistEntryDto weDto2 = validWatchlistEntryDto()
            .withId(we2.id())
            .withImdbID(we2.imdbID())
            .withWatched(we2.watched());

    when(watchlistEntryRepo.findAll()).thenReturn(List.of(we1, we2));
    when(movieService.createMovieResponseDtoFromImdbId(we1.imdbID())).thenReturn(m1);
    when(movieService.createMovieResponseDtoFromImdbId(we2.imdbID())).thenReturn(m2);

    List<WatchlistEntryDto> result = watchlistEntryService.findEntries(null);

    assertEquals(List.of(weDto1, weDto2), result);
    verify(watchlistEntryRepo).findAll();
    verify(movieService).createMovieResponseDtoFromImdbId(we1.imdbID());
    verify(movieService).createMovieResponseDtoFromImdbId(we2.imdbID());
    verifyNoMoreInteractions(watchlistEntryRepo, movieService);
  }

  @Test
  void findEntries_returnsWatchedEntries_whenWatchedIsTrue() {
    WatchlistEntry e1 = validWatchlistEntry().withId("WE-1").withWatched(true);
    MovieResponseDto m1 = validMovieResponseDto();
    WatchlistEntryDto expected = new WatchlistEntryDto(
            e1.id(),
            e1.imdbID(),
            e1.userRating(),
            e1.watched(),
            m1.title(),
            m1.poster(),
            m1.year(),
            m1.type(),
            m1.genre(),
            m1.metascore(),
            m1.imdbRating(),
            m1.plot()
    );

    when(watchlistEntryRepo.findAllByWatched(true)).thenReturn(List.of(e1));
    when(movieService.createMovieResponseDtoFromImdbId(e1.imdbID())).thenReturn(m1);

    List<WatchlistEntryDto> result = watchlistEntryService.findEntries(true);

    assertEquals(List.of(expected), result);
    verify(watchlistEntryRepo).findAllByWatched(true);
    verify(movieService).createMovieResponseDtoFromImdbId(e1.imdbID());
    verifyNoMoreInteractions(watchlistEntryRepo, movieService);
  }

  @Test
  void findEntries_returnsUnwatchedEntries_whenWatchedIsFalse() {
    WatchlistEntry e1 = validWatchlistEntry().withId("WE-1").withWatched(false);
    MovieResponseDto m1 = validMovieResponseDto();
    WatchlistEntryDto d1 = new WatchlistEntryDto(
            e1.id(),
            e1.imdbID(),
            e1.userRating(),
            e1.watched(),
            m1.title(),
            m1.poster(),
            m1.year(),
            m1.type(),
            m1.genre(),
            m1.metascore(),
            m1.imdbRating(),
            m1.plot()
    );

    when(watchlistEntryRepo.findAllByWatched(false)).thenReturn(List.of(e1));
    when(movieService.createMovieResponseDtoFromImdbId(e1.imdbID())).thenReturn(m1);

    List<WatchlistEntryDto> result = watchlistEntryService.findEntries(false);

    assertEquals(List.of(d1), result);
    verify(watchlistEntryRepo).findAllByWatched(false);
    verify(movieService).createMovieResponseDtoFromImdbId(e1.imdbID());
    verifyNoMoreInteractions(watchlistEntryRepo, movieService);
  }

  @Test
  void findEntries_returnsEmptyList_whenNoEntriesExistAndWatchedIsNull() {
    when(watchlistEntryRepo.findAll()).thenReturn(List.of());

    List<WatchlistEntryDto> result = watchlistEntryService.findEntries(null);

    assertEquals(List.of(), result);
    verify(watchlistEntryRepo).findAll();
    verifyNoMoreInteractions(watchlistEntryRepo, movieService);
  }

  @Test
  void findEntries_returnsEmptyList_whenNoWatchedEntriesExist() {
    when(watchlistEntryRepo.findAllByWatched(true)).thenReturn(List.of());

    List<WatchlistEntryDto> result = watchlistEntryService.findEntries(true);

    assertEquals(List.of(), result);
    verify(watchlistEntryRepo).findAllByWatched(true);
    verifyNoMoreInteractions(watchlistEntryRepo, movieService);
  }
}
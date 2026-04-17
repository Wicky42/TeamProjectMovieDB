package org.example.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.example.backend.domain.Watchlist;
import org.example.backend.domain.WatchlistEntry;
import org.example.backend.dto.WatchlistEntryDto;
import org.example.backend.dto.WatchlistResponseDto;
import org.example.backend.exception.DuplicateWatchlistEntryException;
import org.example.backend.exception.WatchlistNotFoundException;
import org.example.backend.repo.WatchlistRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WatchlistServiceTest {
  @Mock IdService idService;
  @Mock WatchlistEntryService watchlistEntryService;
  @Mock WatchlistRepo watchlistRepo;
  @InjectMocks WatchlistService watchlistService;

  private Watchlist validWatchlist() {
    return new Watchlist(
      "W-1",
      "Some name",
      List.of("WE-1", "WE-2"),
      "Some description"
    );
  }

  private WatchlistEntryDto validWatchlistEntryDto() {
    return new WatchlistEntryDto(
      "WE-1",
      "tt1375666",
      "",
      false,
      "Inception",
      "poster-url",
      "2010",
      "movie",
      "Sci-Fi",
      "74",
      "8.8",
      "Plot"
    );
  }

  private WatchlistResponseDto validWatchlistResponseDto() {
    return new WatchlistResponseDto(
      "W-1",
      "Some name",
      List.of(validWatchlistEntryDto()),
      "Some description"
    );
  }

  @Test
  void findAll_returnsRepoFindAll_whenCalled() {
    Watchlist w1 = validWatchlist()
    .withWatchlistEntryIds(List.of("WE-1"));
    Watchlist w2 = validWatchlist()
    .withId("W-2")
    .withWatchlistEntryIds(List.of("WE-2"));

    WatchlistEntryDto we1 = validWatchlistEntryDto().withId("WE-1");
    WatchlistEntryDto we2 = validWatchlistEntryDto().withId("WE-2");

    WatchlistResponseDto wr1 = validWatchlistResponseDto()
      .withEntries(List.of(we1));
    WatchlistResponseDto wr2 = validWatchlistResponseDto()
      .withId("W-2")
      .withEntries(List.of(we2));

    when(watchlistRepo.findAll()).thenReturn(List.of(w1, w2));
    when(watchlistEntryService.createWatchListEntryDtoList(List.of("WE-1")))
      .thenReturn(List.of(we1));
    when(watchlistEntryService.createWatchListEntryDtoList(List.of("WE-2")))
      .thenReturn(List.of(we2));

    assertEquals(List.of(wr1, wr2), watchlistService.findAll());

    verify(watchlistRepo).findAll();
    verify(watchlistEntryService).createWatchListEntryDtoList(List.of("WE-1"));
    verify(watchlistEntryService).createWatchListEntryDtoList(List.of("WE-2"));
    verifyNoMoreInteractions(watchlistRepo, watchlistEntryService);
  }

  @Test
  void findById_returnsWatchlistResponseDto_whenCalledWithValidId() {
    Watchlist watchlist = validWatchlist();
    WatchlistResponseDto watchlistResponse = validWatchlistResponseDto();
    when(watchlistRepo.findById(watchlist.id())).thenReturn(Optional.ofNullable(watchlist));
    when(watchlistEntryService.createWatchListEntryDtoList(watchlist.watchlistEntryIds())).thenReturn(List.of(validWatchlistEntryDto()));

    assertEquals(watchlistResponse, watchlistService.findById(watchlist.id()));
    verify(watchlistRepo).findById(watchlist.id());
    verifyNoMoreInteractions(watchlistRepo);
  }

  @Test
  void findById_throwsWatchlistNotFoundException_whenWatchlistDoesNotExist() {
    when(watchlistRepo.findById("W-1")).thenReturn(Optional.empty());

    assertThrows(
      WatchlistNotFoundException.class,
      () -> watchlistService.findById("W-1")
    );

    verify(watchlistRepo).findById("W-1");
    verifyNoMoreInteractions(idService, watchlistRepo, watchlistEntryService);
  }

  @Test
  void createWatchlist_returnsWatchlistResponseDto_whenNameIsValid() {
    when(idService.generateWatchlistId()).thenReturn("WL-1");

    Watchlist savedWatchlist = new Watchlist(
      "WL-1",
      "My Watchlist",
      List.of(),
      "My description"
    );

    when(watchlistRepo.save(
      new Watchlist(
        "WL-1",
        "My Watchlist",
        List.of(),
        "My description"
      )
    )).thenReturn(savedWatchlist);

    Optional<WatchlistResponseDto> expected = Optional.of(
      new WatchlistResponseDto(
        "WL-1",
        "My Watchlist",
        List.of(),
        "My description"
      )
    );

    assertEquals(expected, watchlistService.createWatchlist("My description", "My Watchlist"));
    verify(idService).generateWatchlistId();
    verify(watchlistRepo).save(
      new Watchlist(
        "WL-1",
        "My Watchlist",
        List.of(),
        "My description"
      )
    );
    verifyNoMoreInteractions(idService, watchlistRepo);
  }

  @Test
  void createWatchlist_returnsEmpty_whenNameIsNull() {
    Optional<WatchlistResponseDto> result = watchlistService.createWatchlist("My description", null);

    assertTrue(result.isEmpty());
    verifyNoMoreInteractions(idService, watchlistRepo);
  }

  @Test
  void createWatchlist_returnsEmpty_whenNameIsBlank() {
    Optional<WatchlistResponseDto> result = watchlistService.createWatchlist("My description", "   ");

    assertTrue(result.isEmpty());
    verifyNoMoreInteractions(idService, watchlistRepo);
  }

 @Test
  void addEntry_throwsWatchlistNotFoundException_whenWatchlistDoesNotExist() {
    when(watchlistRepo.findById("W-1")).thenReturn(Optional.empty());

    assertThrows(
      WatchlistNotFoundException.class,
      () -> watchlistService.addEntry("W-1", "tt1375666")
    );

    verify(watchlistRepo).findById("W-1");
    verifyNoMoreInteractions(idService, watchlistRepo, watchlistEntryService);
  }

  @Test
  void addEntry_throwsDuplicateWatchlistEntryException_whenEntryAlreadyExistsInWatchlist() {
    Watchlist watchlist = new Watchlist(
      "W-1",
      "My Watchlist",
      List.of("WE-1"),
      "My description"
    );

    WatchlistEntry entry = new WatchlistEntry(
      "WE-1",
      "tt1375666",
      "",
      false
    );

    when(watchlistRepo.findById("W-1")).thenReturn(Optional.of(watchlist));
    when(watchlistEntryService.getOrCreateWatchlistEntry("tt1375666")).thenReturn(entry);

    assertThrows(
      DuplicateWatchlistEntryException.class,
      () -> watchlistService.addEntry("W-1", "tt1375666")
    );

    verify(watchlistRepo).findById("W-1");
    verify(watchlistEntryService).getOrCreateWatchlistEntry("tt1375666");
    verifyNoMoreInteractions(idService, watchlistRepo, watchlistEntryService);
  }

  @Test
  void addEntry_returnsWatchlistEntryDto_whenEntryIsAddedSuccessfully() {
    Watchlist watchlist = new Watchlist(
      "W-1",
      "My Watchlist",
      List.of("WE-1"),
      "My description"
    );

    WatchlistEntry entry = new WatchlistEntry(
      "WE-2",
      "tt1375666",
      "",
      false
    );

    Watchlist updatedWatchlist = new Watchlist(
      "W-1",
      "My Watchlist",
      List.of("WE-1", "WE-2"),
      "My description"
    );

    WatchlistEntryDto entryDto = new WatchlistEntryDto(
      "WE-2",
      "tt1375666",
      "",
      false,
      "Inception",
      "poster-url",
      "2010",
      "movie",
      "Sci-Fi",
      "74",
      "8.8",
      "Plot"
    );

    when(watchlistRepo.findById("W-1")).thenReturn(Optional.of(watchlist));
    when(watchlistEntryService.getOrCreateWatchlistEntry("tt1375666")).thenReturn(entry);
    when(watchlistEntryService.toWatchlistEntryDto(entry)).thenReturn(entryDto);
    when(watchlistRepo.save(updatedWatchlist)).thenReturn(updatedWatchlist);

    assertEquals(entryDto, watchlistService.addEntry("W-1", "tt1375666"));

    verify(watchlistRepo).findById("W-1");
    verify(watchlistEntryService).getOrCreateWatchlistEntry("tt1375666");
    verify(watchlistRepo).save(updatedWatchlist);
    verify(watchlistEntryService).toWatchlistEntryDto(entry);
    verifyNoMoreInteractions(idService, watchlistRepo, watchlistEntryService);
  }

 @Test
  void removeEntry_throwsWatchlistNotFoundException_whenWatchlistDoesNotExist() {
    when(watchlistRepo.findById("W-1")).thenReturn(Optional.empty());

    assertThrows(
      WatchlistNotFoundException.class,
      () -> watchlistService.removeEntry("W-1", "WE-1")
    );

    verify(watchlistRepo).findById("W-1");
    verifyNoMoreInteractions(idService, watchlistRepo, watchlistEntryService);
  }

  @Test
  void removeEntry_removesEntrySuccessfully() {
    Watchlist watchlist = new Watchlist(
      "W-1",
      "My Watchlist",
      List.of("WE-1"),
      "My description"
    );

    Watchlist expected = new Watchlist(
      "W-1",
      "My Watchlist",
      List.of(),
      "My description"
    );

    when(watchlistRepo.findById("W-1")).thenReturn(Optional.of(watchlist));

    watchlistService.removeEntry("W-1", "WE-1");

    verify(watchlistRepo).findById("W-1");
    verify(watchlistRepo).save(expected);
    verifyNoMoreInteractions(idService, watchlistRepo, watchlistEntryService);
  }
}

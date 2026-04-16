package org.example.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.example.backend.domain.Watchlist;
import org.example.backend.dto.WatchlistResponseDto;
import org.example.backend.repo.WatchlistRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WatchlistServiceTest {
  @Mock IdService idService;
  @Mock WatchlistRepo watchlistRepo;
  @InjectMocks WatchlistService watchlistService;

  private Watchlist validWatchlist() {
    return new Watchlist(
      "1",
      "Some name",
      List.of("WE-1", "WE2-2"),
      "Some description"
    );
  }

  @Test
  void findAll_returnsRepoFindAll_whenCalled() {
    Watchlist watchlist = validWatchlist();
    when(watchlistRepo.findAll()).thenReturn(List.of(watchlist));

    assertEquals(List.of(watchlist), watchlistService.findAll());
    verify(watchlistRepo).findAll();
    verifyNoMoreInteractions(watchlistRepo);
  }

  @Test
  void findById_returnsRepoFindById_whenCalled() {
    Watchlist watchlist = validWatchlist();
    when(watchlistRepo.findById(watchlist.id())).thenReturn(Optional.ofNullable(watchlist));

    assertEquals(Optional.ofNullable(watchlist), watchlistService.findById(watchlist.id()));
    verify(watchlistRepo).findById(watchlist.id());
    verifyNoMoreInteractions(watchlistRepo);
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
}

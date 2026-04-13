package org.example.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.example.backend.domain.MovieDetails;
import org.example.backend.domain.Watchlist;
import org.example.backend.repo.WatchlistRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WatchlistServiceTest {
  @Mock WatchlistRepo watchlistRepo;
  @InjectMocks WatchlistService watchlistService;

  private Watchlist validWatchlist() {
    return new Watchlist(
      "1",
      "Some name",
      List.of(new MovieDetails(
        "Some title",
        "some-poster-path.jpg",
        2026,
        "Some type",
        "Some imdbID",
        "Action",
        "81",
        "8.3",
        "Plot"
      )),
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
}

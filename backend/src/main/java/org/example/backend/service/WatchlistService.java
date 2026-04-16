package org.example.backend.service;

import java.util.List;
import java.util.Optional;

import org.example.backend.domain.Watchlist;
import org.example.backend.dto.WatchlistResponseDto;
import org.example.backend.repo.WatchlistRepo;
import org.springframework.stereotype.Service;

@Service
public class WatchlistService {
  private final IdService idService;
  private final WatchlistRepo watchlistRepo;

  public WatchlistService(IdService idService, WatchlistRepo watchlistRepo) {
    this.idService = idService;
    this.watchlistRepo = watchlistRepo;
  }

  public List<Watchlist> findAll() {
    return watchlistRepo.findAll();
  }

  public Optional<Watchlist> findById(String id) {
    return watchlistRepo.findById(id);
  }

  public Optional<WatchlistResponseDto> createWatchlist(String description, String name) {
    if (name == null || name.isBlank()) return Optional.empty();

    String id = idService.generateWatchlistId();
    Watchlist saved = watchlistRepo.save(
      new Watchlist (
        id,
        name,
        List.of(),
        description
      )
    );

    return Optional.of(
      new WatchlistResponseDto(
        saved.id(),
        saved.name(),
        List.of(),
        saved.description()
      )
    );
  }
}

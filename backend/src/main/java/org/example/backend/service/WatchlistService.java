package org.example.backend.service;

import java.util.List;
import java.util.Optional;

import org.example.backend.domain.Watchlist;
import org.example.backend.repo.WatchlistRepo;
import org.springframework.stereotype.Service;

@Service
public class WatchlistService {
  private final WatchlistRepo watchlistRepo;

  public WatchlistService(WatchlistRepo watchlistRepo) {
    this.watchlistRepo = watchlistRepo;
  }

  public List<Watchlist> findAll() {
    return watchlistRepo.findAll();
  }

  public Optional<Watchlist> findById(String id) {
    return watchlistRepo.findById(id);
  }
}

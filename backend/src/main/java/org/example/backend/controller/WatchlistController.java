package org.example.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.example.backend.domain.Watchlist;
import org.example.backend.service.WatchlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/watchlists")
class WatchlistController {
  private final WatchlistService watchlistService;

  public WatchlistController(WatchlistService watchlistService) {
    this.watchlistService = watchlistService;
  }

  @GetMapping()
  public ResponseEntity<List<Watchlist>> findAll() {
    return ResponseEntity.ok(watchlistService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Watchlist> findById(@PathVariable String id) {
    return watchlistService.findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}

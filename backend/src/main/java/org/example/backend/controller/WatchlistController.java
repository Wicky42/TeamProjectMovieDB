package org.example.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.example.backend.dto.ImdbIdRequestDto;
import org.example.backend.dto.UpdateWatchlistRequestDto;
import org.example.backend.dto.CreateWatchlistRequestDto;
import org.example.backend.dto.WatchlistEntryDto;
import org.example.backend.dto.WatchlistResponseDto;
import org.example.backend.service.WatchlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/watchlists")
class WatchlistController {
  private final WatchlistService watchlistService;

  public WatchlistController(WatchlistService watchlistService) {
    this.watchlistService = watchlistService;
  }

  @GetMapping()
  public List<WatchlistResponseDto> findAll() {
    return watchlistService.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<WatchlistResponseDto> findById(@PathVariable String id) {
    return ResponseEntity.ok(watchlistService.findById(id));
  }

  @PostMapping()
  public ResponseEntity<WatchlistResponseDto> createWatchlist(@RequestBody CreateWatchlistRequestDto requestData) {
    return watchlistService.createWatchlist(requestData.description(), requestData.name())
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.badRequest().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteWatchlist(@PathVariable String id) {
    watchlistService.deleteWatchlist(id);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}")
  public ResponseEntity<WatchlistResponseDto> updateWatchlist(
    @PathVariable String id,
    @RequestBody UpdateWatchlistRequestDto requestData
  ) {
    return ResponseEntity.ok(watchlistService.updateWatchlist(id, requestData.description(), requestData.name()));
  }

  @PostMapping("/{watchlistId}/entries")
  public WatchlistEntryDto addEntry(
    @PathVariable String watchlistId,
    @RequestBody ImdbIdRequestDto entry
  ) {
    return watchlistService.addEntry(watchlistId, entry.imdbId());
  }

  @DeleteMapping("/{watchlistId}/entries/{entryId}")
  public ResponseEntity<Void> removeEntry(
    @PathVariable String watchlistId,
    @PathVariable String entryId
  ) {
    watchlistService.removeEntry(watchlistId, entryId);
    return ResponseEntity.noContent().build();
  }
}

package org.example.backend.controller;

import org.example.backend.dto.UpdateWatchlistEntryRequestDto;
import org.example.backend.dto.WatchlistEntryDto;
import org.example.backend.service.WatchlistEntryService;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/entries")
public class WatchlistEntryController {
  private final WatchlistEntryService watchlistEntryService;

  public WatchlistEntryController(WatchlistEntryService watchlistEntryService) {
    this.watchlistEntryService = watchlistEntryService;
  }

  @PatchMapping("/{entryId}")
  public WatchlistEntryDto updateEntry(
    @PathVariable String entryId,
    @RequestBody UpdateWatchlistEntryRequestDto requestData
  ) {
    return watchlistEntryService.updateEntry(entryId, requestData);
  }
}

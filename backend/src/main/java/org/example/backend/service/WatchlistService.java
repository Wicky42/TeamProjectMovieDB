package org.example.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.example.backend.domain.Watchlist;
import org.example.backend.domain.WatchlistEntry;
import org.example.backend.dto.WatchlistEntryDto;
import org.example.backend.dto.WatchlistResponseDto;
import org.example.backend.exception.DuplicateWatchlistEntryException;
import org.example.backend.exception.WatchlistNotFoundException;
import org.example.backend.repo.WatchlistRepo;
import org.springframework.stereotype.Service;

@Service
public class WatchlistService {
  private final IdService idService;
  private final WatchlistEntryService watchlistEntryService;
  private final WatchlistRepo watchlistRepo;

  public WatchlistService(
    IdService idService,
    WatchlistRepo watchlistRepo,
    WatchlistEntryService watchlistEntryService
  ) {
    this.idService = idService;
    this.watchlistRepo = watchlistRepo;
    this.watchlistEntryService = watchlistEntryService;
  }

  public List<WatchlistResponseDto> findAll() {
    List<Watchlist> watchlist = watchlistRepo.findAll();
    if (watchlist.isEmpty()) return List.of();

    List<WatchlistResponseDto> responseDtos = new ArrayList<>();
    for (Watchlist w : watchlist) {
      List<WatchlistEntryDto> entryDtos = watchlistEntryService.createWatchListEntryDtoList(w.watchlistEntryIds());
      responseDtos.add(
        new WatchlistResponseDto(
          w.id(),
          w.name(),
          entryDtos,
          w.description()
        )
      );
    }

    return responseDtos;
  }

  public WatchlistResponseDto findById(String id) {
    Watchlist watchlist = watchlistRepo.findById(id)
      .orElseThrow(() -> new WatchlistNotFoundException(id));

    return toWatchlistResponseDto(watchlist);
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

  public void deleteWatchlist(String id) {
    Watchlist watchlist = watchlistRepo.findById(id)
      .orElseThrow(() -> new WatchlistNotFoundException(id));

    watchlistRepo.delete(watchlist);
  }

  public WatchlistResponseDto updateWatchlist(String id, String description, String name) {
    Watchlist watchlist = watchlistRepo.findById(id)
      .orElseThrow(() -> new WatchlistNotFoundException(id));

    Watchlist updated = new Watchlist(
      watchlist.id(),
      name != null ? name : watchlist.name(),
      watchlist.watchlistEntryIds(),
      description != null ? description : watchlist.description()
    );
    watchlistRepo.save(updated);

    return toWatchlistResponseDto(updated);
  }

  public WatchlistEntryDto addEntry(String watchlistId, String imdbId) {
    Watchlist watchlist = watchlistRepo.findById(watchlistId)
      .orElseThrow(() -> new WatchlistNotFoundException(watchlistId));

    WatchlistEntry entry = watchlistEntryService.getOrCreateWatchlistEntry(imdbId);

    if (watchlist.watchlistEntryIds().contains(entry.id())) {
      throw new DuplicateWatchlistEntryException(watchlistId, imdbId);
    }

    List<String> updatedEntryIds = new ArrayList<>(watchlist.watchlistEntryIds());
    updatedEntryIds.add(entry.id());
    watchlistRepo.save(watchlist.withWatchlistEntryIds(updatedEntryIds));

    WatchlistEntryDto entryDto = watchlistEntryService.toWatchlistEntryDto(entry);
    return entryDto;
  }

  public void removeEntry(String watchlistId, String entryId) {
    Watchlist watchlist = watchlistRepo.findById(watchlistId)
      .orElseThrow(() -> new WatchlistNotFoundException(watchlistId));

    List<String> updatedEntryIds = new ArrayList<>(watchlist.watchlistEntryIds());
    updatedEntryIds.remove(entryId);
    watchlistRepo.save(watchlist.withWatchlistEntryIds(updatedEntryIds));
  }

  private WatchlistResponseDto toWatchlistResponseDto(Watchlist watchlist) {
    List<WatchlistEntryDto> entryDtos = watchlistEntryService.createWatchListEntryDtoList(watchlist.watchlistEntryIds());

    return new WatchlistResponseDto(
      watchlist.id(),
      watchlist.name(),
      entryDtos,
      watchlist.description()
    );
  }
}

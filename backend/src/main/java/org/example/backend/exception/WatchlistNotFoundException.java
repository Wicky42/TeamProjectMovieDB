package org.example.backend.exception;

public class WatchlistNotFoundException extends RuntimeException {
  public WatchlistNotFoundException(String watchlistId) {
    super("Watchlist with id " + watchlistId + " not found.");
  }
}

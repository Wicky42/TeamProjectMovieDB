package org.example.backend.exception;

public class WatchlistEntryNotFoundException extends RuntimeException {
  public WatchlistEntryNotFoundException(String entryId) {
    super("WatchlistEntry with id " + entryId + " not found.");
  }
}

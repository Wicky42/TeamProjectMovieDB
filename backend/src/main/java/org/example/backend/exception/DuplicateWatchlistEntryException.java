package org.example.backend.exception;

public class DuplicateWatchlistEntryException extends RuntimeException {
    public DuplicateWatchlistEntryException(String watchlistId, String imdbId) {
        super("Movie with imdbId " + imdbId + " is already in watchlist " + watchlistId + ".");
    }
}

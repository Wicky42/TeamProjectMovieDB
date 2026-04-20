package org.example.backend.exception;

public class DuplicateWatchlistEntryException extends RuntimeException {
    public DuplicateWatchlistEntryException(String watchlistId, String imdbID) {
        super("Movie with imdbID " + imdbID + " is already in watchlist " + watchlistId + ".");
    }
}

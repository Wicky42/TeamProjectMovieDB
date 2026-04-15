package org.example.backend.exception;

public class MovieNotFoundException extends RuntimeException {

    public MovieNotFoundException(String title) {
        super("Film nicht gefunden: " + title);
    }
}

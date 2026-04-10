package org.example.backend.service;

import org.example.backend.client.OmdbClient;
import org.example.backend.domain.Movie;
import org.example.backend.dto.OmdbResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private OmdbClient omdbClient;

    @InjectMocks
    private MovieService movieService;

    @Test
    void shouldReturnMappedMovieWhenOmdbResponseIsSuccessful() {
        OmdbResponseDto dto = new OmdbResponseDto();
        dto.setTitle("Inception");
        dto.setPoster("https://poster.url");
        dto.setYear(2010);
        dto.setType("movie");
        dto.setImdbID("tt1375666");
        dto.setGenre("Action, Sci-Fi");
        dto.setMetascore("74");
        dto.setImdbRating("8.8");
        dto.setResponse("True");

        when(omdbClient.findByTitle("Inception")).thenReturn(dto);

        Movie result = movieService.getMovie("Inception");

        assertNotNull(result);
        assertEquals("Inception", result.title());
        assertEquals("https://poster.url", result.poster());
        assertEquals(2010, result.year());
        assertEquals("movie", result.type());
        assertEquals("tt1375666", result.imdbID());
        assertEquals("Action, Sci-Fi", result.genre());
        assertEquals("74", result.metascore());
        assertEquals("8.8", result.imdbRating());

        verify(omdbClient).findByTitle("Inception");
    }

    @Test
    void shouldThrowExceptionWhenOmdbResponseIsNull() {
        when(omdbClient.findByTitle("Unknown")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> movieService.getMovie("Unknown"));

        assertEquals("Film nicht gefunden", exception.getMessage());
        verify(omdbClient).findByTitle("Unknown");
    }

    @Test
    void shouldThrowExceptionWhenResponseIsFalse() {
        OmdbResponseDto dto = new OmdbResponseDto();
        dto.setResponse("False");

        when(omdbClient.findByTitle("Unknown")).thenReturn(dto);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> movieService.getMovie("Unknown"));

        assertEquals("Film nicht gefunden", exception.getMessage());
        verify(omdbClient).findByTitle("Unknown");
    }
}
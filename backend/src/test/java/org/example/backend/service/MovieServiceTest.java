package org.example.backend.service;

import org.example.backend.client.OmdbClient;
import org.example.backend.domain.MovieDetails;
import org.example.backend.dto.OmdbMovieDetailsDto;
import org.example.backend.dto.OmdbMovieDto;
import org.example.backend.dto.OmdbSearchResponseDto;
import org.example.backend.exception.MovieNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private OmdbClient omdbClient;

    @InjectMocks
    private MovieService movieService;

    private static final String VALID_TITLE = "Inception";
    private static final String VALID_IMDB_ID = "tt1375666";

    private OmdbMovieDetailsDto createValidDetailsDto() {
        OmdbMovieDetailsDto dto = new OmdbMovieDetailsDto();
        dto.setTitle("Inception");
        dto.setPoster("poster.jpg");
        dto.setYear(2010);
        dto.setType("movie");
        dto.setImdbID(VALID_IMDB_ID);
        dto.setGenre("Sci-Fi");
        dto.setMetascore("74");
        dto.setImdbRating("8.8");
        dto.setPlot("Dreams...");
        dto.setResponse("True");
        return dto;
    }

    @Test
    void retrieveMovieDetailsByTitle_shouldReturnMovieDetails() {
        OmdbMovieDetailsDto dto = createValidDetailsDto();
        when(omdbClient.findByTitle(VALID_TITLE)).thenReturn(dto);

        MovieDetails result = movieService.retrieveMovieDetailsByTitle(VALID_TITLE);

        assertEquals("Inception", result.title());
        assertEquals("poster.jpg", result.poster());
        assertEquals(2010, result.year());
        assertEquals("movie", result.type());
        assertEquals(VALID_IMDB_ID, result.imdbID());
        assertEquals("Sci-Fi", result.genre());
        assertEquals("74", result.metascore());
        assertEquals("8.8", result.imdbRating());
        assertEquals("Dreams...", result.plot());
    }

    @Test
    void retrieveMovieDetailsByTitle_shouldThrowMovieNotFoundException_whenNotFound() {
        OmdbMovieDetailsDto dto = new OmdbMovieDetailsDto();
        dto.setResponse("False");
        when(omdbClient.findByTitle(VALID_TITLE)).thenReturn(dto);

        assertThrows(MovieNotFoundException.class,
                () -> movieService.retrieveMovieDetailsByTitle(VALID_TITLE));
    }

    @Test
    void retrieveMovies_shouldReturnMovieDetailsList() {
        OmdbMovieDto movieDto = new OmdbMovieDto();
        movieDto.setTitle("Inception");
        movieDto.setImdbID(VALID_IMDB_ID);

        OmdbSearchResponseDto searchResponse = new OmdbSearchResponseDto();
        searchResponse.setSearch(List.of(movieDto));

        OmdbMovieDetailsDto detailsDto = createValidDetailsDto();

        when(omdbClient.findMovies(VALID_TITLE)).thenReturn(searchResponse);
        when(omdbClient.findByImdbId(VALID_IMDB_ID)).thenReturn(detailsDto);

        List<MovieDetails> result = movieService.retrieveMovies(VALID_TITLE);

        assertEquals(1, result.size());
        assertEquals("Inception", result.getFirst().title());
    }
}

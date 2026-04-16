package org.example.backend.service;

import org.example.backend.client.OmdbClient;
import org.example.backend.client.OpenAIResponseException;
import org.example.backend.client.OpenAiClient;
import org.example.backend.domain.MovieDetails;
import org.example.backend.dto.*;
import org.example.backend.exception.MovieNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private OmdbClient omdbClient;

    @Mock
    private OpenAiClient openAiClient;

    @InjectMocks
    private MovieService movieService;

    private static final String VALID_TITLE = "Inception";
    private static final String VALID_IMDB_ID = "tt1375666";

    private OmdbMovieDetailsDto createValidDetailsDto() {
        OmdbMovieDetailsDto dto = new OmdbMovieDetailsDto();
        dto.setTitle("Inception");
        dto.setPoster("poster.jpg");
        dto.setYear("2010");
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
        assertEquals("2010", result.year());
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

    @Test
    void getMovieFromAiSuggestion_shouldReturnMovieDetails_whenAiReturnsValidImdbId() {
        String prompt = "Ich möchte einen Film über Träume";
        OmdbMovieDetailsDto detailsDto = createValidDetailsDto();

        when(openAiClient.findMovieImdbID_whenCalledWithPrompt(prompt))
                .thenReturn(createOpenAiResponse("tt1375666"));
        when(omdbClient.findByImdbId("tt1375666"))
                .thenReturn(detailsDto);

        MovieDetails result = movieService.getMovieFromAiSuggestion(prompt);

        assertEquals("Inception", result.title());
        assertEquals("tt1375666", result.imdbID());
        assertEquals("Sci-Fi", result.genre());

        verify(openAiClient).findMovieImdbID_whenCalledWithPrompt(prompt);
        verify(omdbClient).findByImdbId("tt1375666");
    }

    @Test
    void getMovieFromAiSuggestion_shouldThrowOpenAIResponseException_whenAiReturnsInvalidText() {
        String prompt = "Ich möchte einen Film über Träume";

        when(openAiClient.findMovieImdbID_whenCalledWithPrompt(prompt))
                .thenReturn(createOpenAiResponse("Inception"));

        OpenAIResponseException exception = assertThrows(
                OpenAIResponseException.class,
                () -> movieService.getMovieFromAiSuggestion(prompt)
        );

        assertEquals("Kein Film zum Prompt gefunden", exception.getMessage());
        verify(omdbClient, never()).findByImdbId(anyString());
    }

    @Test
    void getMovieFromAiSuggestion_shouldThrowOpenAIResponseException_whenAiReturnsNull() {
        String prompt = "Ich möchte einen Film über Träume";

        when(openAiClient.findMovieImdbID_whenCalledWithPrompt(prompt))
                .thenReturn(createOpenAiResponse(null));

        OpenAIResponseException exception = assertThrows(
                OpenAIResponseException.class,
                () -> movieService.getMovieFromAiSuggestion(prompt)
        );

        assertEquals("Kein Film zum Prompt gefunden", exception.getMessage());
        verify(omdbClient, never()).findByImdbId(anyString());
    }

    private OpenAIResponse createOpenAiResponse(String content) {
        return new OpenAIResponse(
                "gpt-5.4",
                List.of(new OpenAIChoice(
                        0,
                        new OpenAIMessage("assistant", content)
                ))
        );
    }

}

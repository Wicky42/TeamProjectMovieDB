package org.example.backend.client;

import org.example.backend.dto.OmdbMovieDetailsDto;
import org.example.backend.dto.OmdbSearchResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OmdbClientTest {

    private OmdbClient omdbClient;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private static final String API_KEY = "test-api-key";
    private static final String VALID_TITLE = "Inception";
    private static final String VALID_IMDB_ID = "tt1375666";

    @BeforeEach
    void setUp() {
        omdbClient = new OmdbClient(API_KEY);

        ReflectionTestUtils.setField(omdbClient, "restClient", restClient);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void findByTitle_shouldReturnMovieDetails() {
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

        when(responseSpec.body(OmdbMovieDetailsDto.class)).thenReturn(dto);

        OmdbMovieDetailsDto result = omdbClient.findByTitle(VALID_TITLE);

        assertNotNull(result);
        assertEquals("Inception", result.getTitle());
        assertEquals("poster.jpg", result.getPoster());
        assertEquals(2010, result.getYear());
        assertEquals("movie", result.getType());
        assertEquals(VALID_IMDB_ID, result.getImdbID());
        assertEquals("Sci-Fi", result.getGenre());
        assertEquals("74", result.getMetascore());
        assertEquals("8.8", result.getImdbRating());
        assertEquals("Dreams...", result.getPlot());
    }

    @Test
    void findByTitle_shouldThrowException_whenResponseIsNull() {
        when(responseSpec.body(OmdbMovieDetailsDto.class)).thenReturn(null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> omdbClient.findByTitle(VALID_TITLE)
        );

        assertEquals("Keine Antwort von OMDb", exception.getMessage());
    }

    @Test
    void findByTitle_shouldThrowException_whenResponseIsFalse() {
        OmdbMovieDetailsDto dto = new OmdbMovieDetailsDto();
        dto.setResponse("False");
        dto.setError("Movie not found!");

        when(responseSpec.body(OmdbMovieDetailsDto.class)).thenReturn(dto);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> omdbClient.findByTitle("Unknown")
        );

        assertEquals("OMDb Fehler: Movie not found!", exception.getMessage());
    }

    @Test
    void findByImdbId_shouldReturnMovieDetails() {
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

        when(responseSpec.body(OmdbMovieDetailsDto.class)).thenReturn(dto);

        OmdbMovieDetailsDto result = omdbClient.findByImdbId(VALID_IMDB_ID);

        assertNotNull(result);
        assertEquals("Inception", result.getTitle());
        assertEquals(VALID_IMDB_ID, result.getImdbID());
    }

    @Test
    void findMovies_shouldReturnSearchResponse() {
        OmdbSearchResponseDto dto = new OmdbSearchResponseDto();
        dto.setResponse("True");

        when(responseSpec.body(OmdbSearchResponseDto.class)).thenReturn(dto);

        OmdbSearchResponseDto result = omdbClient.findMovies(VALID_TITLE);

        assertNotNull(result);
        assertEquals("True", result.getResponse());
    }

    @Test
    void findMovies_shouldThrowException_whenResponseIsNull() {
        when(responseSpec.body(OmdbSearchResponseDto.class)).thenReturn(null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> omdbClient.findMovies(VALID_TITLE)
        );

        assertEquals("Keine Antwort von OMDb", exception.getMessage());
    }

    @Test
    void findMovies_shouldThrowException_whenResponseIsFalse() {
        OmdbSearchResponseDto dto = new OmdbSearchResponseDto();
        dto.setResponse("False");

        when(responseSpec.body(OmdbSearchResponseDto.class)).thenReturn(dto);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> omdbClient.findMovies("Unknown")
        );

        assertEquals("OMDb Fehler: ", exception.getMessage());
    }
}
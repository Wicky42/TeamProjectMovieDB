package org.example.backend.client;

import org.example.backend.dto.OmdbMovieDetailsDto;
import org.example.backend.dto.OmdbSearchResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OmdbClient {

    private static final String BASE_URL = "http://www.omdbapi.com";
    private static final String OMDB_RESPONSE_SUCCESS = "True";

    private final RestClient restClient;
    private final String apiKey;

    public OmdbClient(@Value("${omdb.api}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .build();
    }

    public OmdbMovieDetailsDto findByTitle(String title) {
        OmdbMovieDetailsDto response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("apikey", apiKey)
                        .queryParam("t", title)
                        .build())
                .retrieve()
                .body(OmdbMovieDetailsDto.class);

        validateMovieDetailsResponse(response);
        return response;
    }

    public OmdbMovieDetailsDto findByImdbId(String imdbId) {
        OmdbMovieDetailsDto response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("apikey", apiKey)
                        .queryParam("i", imdbId)
                        .build())
                .retrieve()
                .body(OmdbMovieDetailsDto.class);

        validateMovieDetailsResponse(response);
        return response;
    }

    public OmdbSearchResponseDto findMovies(String title) {
        OmdbSearchResponseDto response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("apikey", apiKey)
                        .queryParam("s", title)
                        .build())
                .retrieve()
                .body(OmdbSearchResponseDto.class);

        validateSearchResponse(response);
        return response;
    }

    private void validateMovieDetailsResponse(OmdbMovieDetailsDto response) {
        if (response == null) {
            throw new OmdbClientException("Keine Antwort von OMDb");
        }
        if (!OMDB_RESPONSE_SUCCESS.equalsIgnoreCase(response.getResponse())) {
            throw new OmdbClientException("OMDb Fehler: " + response.getError());
        }
    }

    private void validateSearchResponse(OmdbSearchResponseDto response) {
        if (response == null) {
            throw new OmdbClientException("Keine Antwort von OMDb");
        }
        if (!OMDB_RESPONSE_SUCCESS.equalsIgnoreCase(response.getResponse())) {
            throw new OmdbClientException("OMDb Fehler: Keine Suchergebnisse gefunden");
        }
    }
}

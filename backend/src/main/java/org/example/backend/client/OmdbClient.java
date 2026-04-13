package org.example.backend.client;

import org.example.backend.dto.OmdbMovieDetailsDto;
import org.example.backend.dto.OmdbSearchResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OmdbClient {

    private final RestClient restClient;
    private final String apiKey;

    public OmdbClient( @Value("${omdb.api}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .baseUrl("http://www.omdbapi.com")
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

        if (response == null) {
            throw new RuntimeException("Keine Antwort von OMDb");
        }

        if (!"True".equalsIgnoreCase(response.getResponse())) {
            throw new RuntimeException("OMDb Fehler: " + response.getError());
        }

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

        if (response == null) {
            throw new RuntimeException("Keine Antwort von OMDb");
        }

        if (!"True".equalsIgnoreCase(response.getResponse())) {
            throw new RuntimeException("OMDb Fehler: " + response.getError());
        }

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

        if (response == null) {
            throw new RuntimeException("Keine Antwort von OMDb");
        }

        if (!"True".equalsIgnoreCase(response.getResponse())) {
            throw new RuntimeException("OMDb Fehler: ");
        }

        return response;
    }
}

package org.example.backend.client;

import org.example.backend.dto.OmdbResponseDto;
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

    public OmdbResponseDto findByTitle(String title) {
        OmdbResponseDto response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("apikey", apiKey)
                        .queryParam("t", title)
                        .build())
                .retrieve()
                .body(OmdbResponseDto.class);

        if (response == null) {
            throw new RuntimeException("Keine Antwort von OMDb");
        }

        if (!"True".equalsIgnoreCase(response.getResponse())) {
            throw new RuntimeException("OMDb Fehler: " + response.getError());
        }

        return response;
    }


}

package org.example.backend.service;

import org.example.backend.client.OmdbClient;
import org.example.backend.client.OpenAIResponseException;
import org.example.backend.client.OpenAiClient;
import org.example.backend.dto.MovieResponseDto;
import org.example.backend.dto.OmdbMovieDetailsDto;
import org.example.backend.dto.OmdbSearchResponseDto;
import org.example.backend.exception.MovieNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    private final OmdbClient omdbClient;
    private final OpenAiClient openAiClient;

    public MovieService(OmdbClient omdbClient, OpenAiClient openAiClient) {
        this.omdbClient = omdbClient;
        this.openAiClient = openAiClient;
    }

    public MovieResponseDto retrieveMovieDetailsByTitle(String title) {
        OmdbMovieDetailsDto response = omdbClient.findByTitle(title);

        if (response == null || !"True".equalsIgnoreCase(response.getResponse())) {
            throw new MovieNotFoundException(title);
        }

        return toMovieDetails(response);
    }

    public List<MovieResponseDto> retrieveMovies(String title) {
        OmdbSearchResponseDto searchResponse = omdbClient.findMovies(title);

        return searchResponse.getSearch().stream()
                .map(movie -> omdbClient.findByImdbId(movie.getImdbID()))
                .map(this::toMovieDetails)
                .toList();
    }

    public MovieResponseDto getMovieFromAiSuggestion(String prompt) {
        String openAiResponse = openAiClient.findMovieImdbID_whenCalledWithPrompt(prompt).text();

        if( !validateAiResponse(openAiResponse)) {
            throw new OpenAIResponseException("Kein Film zum Prompt gefunden");
        }
        return toMovieDetails(omdbClient.findByImdbId(openAiResponse));
    }

    //* --------------- HELPER -------------*//

    private MovieResponseDto toMovieDetails(OmdbMovieDetailsDto dto) {
        return new MovieResponseDto(
                dto.getTitle(),
                dto.getPoster(),
                dto.getYear(),
                dto.getType(),
                dto.getImdbID(),
                dto.getGenre(),
                dto.getMetascore(),
                dto.getImdbRating(),
                dto.getPlot()
        );
    }

    private static boolean validateAiResponse(String openAiResponse) {
        if (openAiResponse == null) {
            return false;
        }
        return openAiResponse.matches("tt\\d+");
    }
}

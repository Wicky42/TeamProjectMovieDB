package org.example.backend.service;

import org.example.backend.client.OmdbClient;
import org.example.backend.domain.MovieDetails;
import org.example.backend.dto.OmdbMovieDetailsDto;
import org.example.backend.dto.OmdbSearchResponseDto;
import org.example.backend.exception.MovieNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    private final OmdbClient omdbClient;

    public MovieService(OmdbClient omdbClient) {
        this.omdbClient = omdbClient;
    }

    public MovieDetails retrieveMovieDetailsByTitle(String title) {
        OmdbMovieDetailsDto response = omdbClient.findByTitle(title);

        if (response == null || !"True".equalsIgnoreCase(response.getResponse())) {
            throw new MovieNotFoundException(title);
        }

        return toMovieDetails(response);
    }

    public List<MovieDetails> retrieveMovies(String title) {
        OmdbSearchResponseDto searchResponse = omdbClient.findMovies(title);

        return searchResponse.getSearch().stream()
                .map(movie -> omdbClient.findByImdbId(movie.getImdbID()))
                .map(this::toMovieDetails)
                .toList();
    }

    private MovieDetails toMovieDetails(OmdbMovieDetailsDto dto) {
        return new MovieDetails(
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
}

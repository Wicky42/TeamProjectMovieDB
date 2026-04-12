package org.example.backend.service;

import org.example.backend.client.OmdbClient;
import org.example.backend.domain.Movie;
import org.example.backend.domain.MovieDetails;
import org.example.backend.dto.OmdbMovieDetailsDto;
import org.example.backend.dto.OmdbMovieDto;
import org.example.backend.dto.OmdbSearchResponseDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MovieService {

    private final OmdbClient omdbClient;

    public MovieService( OmdbClient omdbClient) {
        this.omdbClient = omdbClient;
    }

    public MovieDetails retrieveMovieDetailsByTitle(String title) {
        OmdbMovieDetailsDto response = omdbClient.findByTitle(title);

        if (response == null || !"True".equalsIgnoreCase(response.getResponse())) {
            throw new RuntimeException("Film nicht gefunden");
        }

        return mapToMovieDetails(response);
    }

    public List<Movie> retrieveMovies(String title){
        OmdbSearchResponseDto omdbSearchResponseDto = omdbClient.findMovies(title);

        return mapToMovie(omdbSearchResponseDto);
    }

    //*--------- HELP MAP TO MOVIE ------*//
    private MovieDetails mapToMovieDetails(OmdbMovieDetailsDto dto){
        return new MovieDetails(
                dto.getTitle(),
                dto.getPoster(),
                dto.getYear(),
                dto.getType(),
                dto.getImdbID(),
                dto.getGenre(),
                dto.getMetascore(),
                dto.getImdbRating()
        );
    }

    private List<Movie> mapToMovie(OmdbSearchResponseDto searchDto){
        List<Movie> movies = new ArrayList<>();
        for( OmdbMovieDto dto : searchDto.getSearch() ){
            Movie toAdd = new Movie(
                    dto.getTitle(),
                    dto.getYear(),
                    dto.getImdbID(),
                    dto.getType(),
                    dto.getPoster()
            );
            movies.add(toAdd);
        }
        return movies;
    }
}

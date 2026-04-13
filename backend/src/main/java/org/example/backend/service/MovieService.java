package org.example.backend.service;

import org.example.backend.client.OmdbClient;
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

    public List<MovieDetails> retrieveMovies(String title){
        OmdbSearchResponseDto omdbSearchResponseDto = omdbClient.findMovies(title);

        return mapToMovieDetails(omdbSearchResponseDto);
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
                dto.getImdbRating(),
                dto.getPlot()
        );
    }


    private List<MovieDetails> mapToMovieDetails(OmdbSearchResponseDto searchResponseDto){
        List<MovieDetails> movieDetailsList = new ArrayList<>();
        for(OmdbMovieDto movieDto : searchResponseDto.getSearch()){
            OmdbMovieDetailsDto movieDetailDto = omdbClient.findByImdbId(movieDto.getImdbID());
            MovieDetails movieToAdd = mapToMovieDetails(movieDetailDto);
            movieDetailsList.add(movieToAdd);
        }
        return movieDetailsList;
    }
}

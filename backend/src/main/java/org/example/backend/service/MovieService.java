package org.example.backend.service;

import org.example.backend.client.OmdbClient;
import org.example.backend.domain.Movie;
import org.example.backend.dto.OmdbResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class MovieService {

    private final OmdbClient omdbClient;

    public MovieService( OmdbClient omdbClient) {
        this.omdbClient = omdbClient;
    }

    public Movie getMovie(String title) {
        OmdbResponseDto response = omdbClient.findByTitle(title);

        if (response == null || !"True".equalsIgnoreCase(response.getResponse())) {
            throw new RuntimeException("Film nicht gefunden");
        }

        return mapToMovie(response);

    }

    //*--------- HELP MAP TO MOVIE ------*//
    private Movie mapToMovie(OmdbResponseDto dto){
        return new Movie(
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
}

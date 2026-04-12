package org.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OmdbMovieDetailsDto {

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Year")
    private int year;

    @JsonProperty("Genre")
    private String genre;

    @JsonProperty("Director")
    private String director;

    @JsonProperty("Plot")
    private String plot;

    @JsonProperty("Poster")
    private String poster;

    @JsonProperty("Response")
    private String response;

    @JsonProperty("Error")
    private String error;

    @JsonProperty("Type")
    private String type;

    private String imdbID;

    @JsonProperty("Metascore")
    private String metascore;

    private String imdbRating;

    public boolean isSuccess() {
        return "True".equalsIgnoreCase(response);
    }
}

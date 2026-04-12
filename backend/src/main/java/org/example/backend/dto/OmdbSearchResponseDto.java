package org.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OmdbSearchResponseDto {
    @JsonProperty("Search")
    List<OmdbMovieDto> search;

    @JsonProperty("Response")
    String response;
}

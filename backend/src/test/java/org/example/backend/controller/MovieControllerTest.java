package org.example.backend.controller;

import org.example.backend.domain.MovieDetails;
import org.example.backend.service.MovieService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(MovieController.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    private static final String VALID_TITLE = "Inception";

    private static final MovieDetails VALID_DATA = new MovieDetails(
            "Inception",
            "https://image.url/inception.jpg",
            2010,
            "movie",
            "tt1375666",
            "Sci-Fi",
            "74",
            "8.8",
            "A thief who steals corporate secrets through dream-sharing technology."
    );

    private static final List<MovieDetails> VALID_LIST = List.of(
            VALID_DATA,
            new MovieDetails(
                    "Interstellar",
                    "https://image.url/interstellar.jpg",
                    2014,
                    "movie",
                    "tt0816692",
                    "Sci-Fi",
                    "74",
                    "8.6",
                    "A team travels through a wormhole in space."
            )
    );

    @Test
    void getMovieByTitle_shouldReturnMovieDetails() throws Exception {
        when(movieService.retrieveMovieDetailsByTitle(VALID_TITLE))
                .thenReturn(VALID_DATA);

        mockMvc.perform(get("/api/movies/{title}", VALID_TITLE))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.title").value(VALID_DATA.title()))
                .andExpect(jsonPath("$.poster").value(VALID_DATA.poster()))
                .andExpect(jsonPath("$.year").value(VALID_DATA.year()))
                .andExpect(jsonPath("$.type").value(VALID_DATA.type()))
                .andExpect(jsonPath("$.imdbID").value(VALID_DATA.imdbID()))
                .andExpect(jsonPath("$.genre").value(VALID_DATA.genre()))
                .andExpect(jsonPath("$.metascore").value(VALID_DATA.metascore()))
                .andExpect(jsonPath("$.imdbRating").value(VALID_DATA.imdbRating()))
                .andExpect(jsonPath("$.plot").value(VALID_DATA.plot()));
    }

    @Test
    void getMoviesByTitle_shouldReturnMovieList() throws Exception {
        when(movieService.retrieveMovies(VALID_TITLE))
                .thenReturn(VALID_LIST);

        mockMvc.perform(get("/api/movies")
                        .param("title", VALID_TITLE))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].title").value(VALID_DATA.title()))
                .andExpect(jsonPath("$[0].poster").value(VALID_DATA.poster()))
                .andExpect(jsonPath("$[0].year").value(VALID_DATA.year()))
                .andExpect(jsonPath("$[1].title").value("Interstellar"));
    }

    @Test
    void getMoviesByTitle_withoutTitleParam_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isBadRequest());
    }
}
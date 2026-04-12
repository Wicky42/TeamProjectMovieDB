package org.example.backend.controller;

import org.example.backend.domain.MovieDetails;
import org.example.backend.service.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Nur den Controller testen
@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(MovieController.class)
class MovieDetailsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    @Test
    void shouldReturnMovieByTitle() throws Exception {
        MovieDetails movieDetails = new MovieDetails(
                "Some title",
                "some-poster-path.jpg",
                2026,
                "Some type",
                "Some imdbID",
                "Action",
                "81",
                "8.3");

        when(movieService.retrieveMovieDetailsByTitle("Some title")).thenReturn(movieDetails);

        mockMvc.perform(get("/api/movies")
                        .param("title", "Some title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Some title"))
                .andExpect(jsonPath("$.year").value("2026"))
                .andExpect(jsonPath("$.genre").value("Action"));
    }

    @Test
    void shouldReturn400WhenTitleMissing() throws Exception {
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isBadRequest());
    }


}
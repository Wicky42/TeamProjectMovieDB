package org.example.backend.controller;

import jakarta.annotation.Nonnull;
import org.example.backend.domain.Movie;
import org.example.backend.domain.MovieDetails;
import org.example.backend.service.MovieService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("{title}")
    public MovieDetails getMovieByTitle(@PathVariable String title){
        return movieService.retrieveMovieDetailsByTitle(title);
    }

    @GetMapping
    public List<Movie> getMoviesByTitle(@RequestParam @Nonnull String title){
        return movieService.retrieveMovies(title);
    }
}

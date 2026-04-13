package org.example.backend.controller;

import org.example.backend.domain.MovieDetails;
import org.example.backend.service.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
class MovieController {

    private final MovieService movieService;

    MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/{title}")
    public ResponseEntity<MovieDetails> getMovieByTitle(@PathVariable String title) {
        return ResponseEntity.ok(movieService.retrieveMovieDetailsByTitle(title));
    }

    @GetMapping
    public ResponseEntity<List<MovieDetails>> getMoviesByTitle(@RequestParam String title) {
        return ResponseEntity.ok(movieService.retrieveMovies(title));
    }
}

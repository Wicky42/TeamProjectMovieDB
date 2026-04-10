package org.example.backend.controller;

import java.util.List;

import org.example.backend.domain.Movie;
import org.example.backend.domain.Watchlist;
import org.example.backend.repo.WatchlistRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
public class WatchlistControllerTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private WatchlistRepo watchlistRepo;

  private Watchlist validWatchlist() {
    return new Watchlist(
      "1",
      "Some name",
      List.of(new Movie(
        "Some title",
        "some-poster-path.jpg",
        2026,
        "Some type",
        "Some imdbID",
        "Action",
        "81",
        "8.3"
      )),
      "Some description"
    );
  }

  @Test
  void findAll_returnsOkAndEmptyList_whenNoWatchlistInDb () throws Exception {
    mockMvc.perform(get("/api/watchlists"))
      .andExpect(status().isOk())
      .andExpect(content().json("[]"));
  }

  @Test
  void findAll_returnsOkAndListOfWatchlists_whenWatchlistsPresentInDb () throws Exception {
    List<Watchlist> expectedWatchlists = List.of(validWatchlist(), validWatchlist().withId("2"));
    expectedWatchlists.forEach(watchlist -> watchlistRepo.save(watchlist));

    mockMvc.perform(get("/api/watchlists"))
      .andExpect(status().isOk())
      .andExpect(content().json(objectMapper.writeValueAsString(expectedWatchlists)));
  }

  @Test
  void findById_returnsOkAndWatchlist_whenQueriedWatchlistExists () throws Exception {
    Watchlist watchlist = validWatchlist();
    watchlistRepo.save(watchlist);

    mockMvc.perform(get("/api/watchlists/" + watchlist.id()))
      .andExpect(status().isOk())
      .andExpect(content().json(objectMapper.writeValueAsString(watchlist)));
  }

  @Test
  void findById_returnsNotFound_whenQueriedWatchlistNotExists () throws Exception {
    Watchlist watchlist = validWatchlist();

    mockMvc.perform(get("/api/watchlists/" + watchlist.id()))
      .andExpect(status().isNotFound());
  }
}

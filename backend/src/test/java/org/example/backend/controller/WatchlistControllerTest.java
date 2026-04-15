package org.example.backend.controller;

import java.util.List;
import java.util.Optional;

import org.example.backend.domain.MovieDetails;
import org.example.backend.domain.Watchlist;
import org.example.backend.service.WatchlistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WatchlistController.class)
@AutoConfigureMockMvc
class WatchlistControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private WatchlistService watchlistService;

  private Watchlist validWatchlist() {
    return new Watchlist(
            "1",
            "Some name",
            List.of(new MovieDetails(
                    "Some title",
                    "some-poster-path.jpg",
                    2026,
                    "Some type",
                    "Some imdbID",
                    "Action",
                    "81",
                    "8.3",
                    "Plot"
            )),
            "Some description"
    );
  }

  @Test
  void findAll_returnsOkAndEmptyList_whenNoWatchlistInDb() throws Exception {
    when(watchlistService.findAll()).thenReturn(List.of());

    mockMvc.perform(get("/api/watchlists"))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
  }

  @Test
  void findAll_returnsOkAndListOfWatchlists_whenWatchlistsPresentInDb() throws Exception {
    List<Watchlist> expectedWatchlists =
            List.of(validWatchlist(), validWatchlist().withId("2"));

    when(watchlistService.findAll()).thenReturn(expectedWatchlists);

    mockMvc.perform(get("/api/watchlists"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(expectedWatchlists)));
  }

  @Test
  void findById_returnsOkAndWatchlist_whenQueriedWatchlistExists() throws Exception {
    Watchlist watchlist = validWatchlist();

    when(watchlistService.findById("1")).thenReturn(Optional.of(watchlist));

    mockMvc.perform(get("/api/watchlists/1"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(watchlist)));
  }
}
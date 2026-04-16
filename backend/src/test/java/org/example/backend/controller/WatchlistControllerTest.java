package org.example.backend.controller;

import java.util.List;
import java.util.Optional;

import org.example.backend.domain.Watchlist;
import org.example.backend.dto.AddWatchlistEntryRequestDto;
import org.example.backend.dto.CreateWatchlistRequestDto;
import org.example.backend.dto.WatchlistEntryDto;
import org.example.backend.dto.WatchlistResponseDto;
import org.example.backend.exception.DuplicateWatchlistEntryException;
import org.example.backend.exception.WatchlistNotFoundException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
      "W-1",
      "Some name",
      List.of("WE-1", "WE2-2"),
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

  @Test
  void findById_returnsNotFound_whenQueriedWatchlistDoesNotExist() throws Exception {
    when(watchlistService.findById("999")).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/watchlists/999"))
      .andExpect(status().isNotFound());
  }

  @Test
  void createWatchlist_returnsOkAndWatchlistResponseDto_whenRequestDataIsValid() throws Exception {
    CreateWatchlistRequestDto request = new CreateWatchlistRequestDto(
      "My description",
      "My Watchlist"
    );

    WatchlistResponseDto response = new WatchlistResponseDto(
      "W-1",
      "My Watchlist",
      List.of(),
      "My description"
    );

    when(watchlistService.createWatchlist("My description", "My Watchlist"))
      .thenReturn(Optional.of(response));

    mockMvc.perform(post("/api/watchlists")
      .contentType("application/json")
      .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andExpect(content().json(objectMapper.writeValueAsString(response)));
  }

  @Test
  void createWatchlist_returnsBadRequest_whenRequestDataIsInvalid() throws Exception {
    CreateWatchlistRequestDto request = new CreateWatchlistRequestDto(
      "   ",
      "My description"
    );

    when(watchlistService.createWatchlist("My description", "   "))
      .thenReturn(Optional.empty());

    mockMvc.perform(post("/api/watchlists")
      .contentType("application/json")
      .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest());
  }

  @Test
  void addEntry_returnsOkAndWatchlistEntryDto_whenCalledWithValidData() throws Exception {
    AddWatchlistEntryRequestDto request = new AddWatchlistEntryRequestDto("tt1375666");

    WatchlistEntryDto response = new WatchlistEntryDto(
      "WE-1",
      "tt1375666",
      "",
      false,
      "Inception",
      "poster-url",
      "2010",
      "movie",
      "Sci-Fi",
      "74",
      "8.8",
      "Plot"
    );

    when(watchlistService.addEntry("W-1", "tt1375666")).thenReturn(response);

    mockMvc.perform(post("/api/watchlists/W-1/entries")
      .contentType("application/json")
      .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andExpect(content().json(objectMapper.writeValueAsString(response)));
  }

  @Test
  void addEntry_returnsNotFound_whenWatchlistDoesNotExist() throws Exception {
    AddWatchlistEntryRequestDto request = new AddWatchlistEntryRequestDto("tt1375666");

    when(watchlistService.addEntry("W-404", "tt1375666"))
      .thenThrow(new WatchlistNotFoundException("W-404"));

    mockMvc.perform(post("/api/watchlists/W-404/entries")
      .contentType("application/json")
      .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isNotFound());
  }

  @Test
  void addEntry_returnsConflict_whenMovieAlreadyExistsInWatchlist() throws Exception {
    AddWatchlistEntryRequestDto request = new AddWatchlistEntryRequestDto("tt1375666");

    when(watchlistService.addEntry("W-1", "tt1375666"))
      .thenThrow(new DuplicateWatchlistEntryException("W-1", "tt1375666"));

    mockMvc.perform(post("/api/watchlists/W-1/entries")
      .contentType("application/json")
      .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isConflict());
  }
}
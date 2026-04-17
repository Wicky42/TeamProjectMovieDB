package org.example.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import org.springframework.http.MediaType;
import org.example.backend.dto.UpdateWatchlistEntryRequestDto;
import org.example.backend.dto.WatchlistEntryDto;
import org.example.backend.exception.WatchlistEntryNotFoundException;
import org.example.backend.service.WatchlistEntryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WatchlistEntryController.class)
@AutoConfigureMockMvc
public class WatchlistEntryControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private WatchlistEntryService watchlistEntryService;

  @Test
  void updateEntry_returnsUpdatedWatchlistEntryDto_whenRequestIsValid() throws Exception {
    WatchlistEntryDto response = new WatchlistEntryDto(
      "WE-1",
      "tt1375666",
      "9",
      true,
      "Inception",
      "poster-url",
      "2010",
      "movie",
      "Sci-Fi",
      "74",
      "8.8",
      "Plot"
    );

    when(watchlistEntryService.updateEntry(
      eq("WE-1"),
      eq(new UpdateWatchlistEntryRequestDto("9", true))
    )).thenReturn(response);

    mockMvc.perform(patch("/api/entries/WE-1")
      .contentType(MediaType.APPLICATION_JSON)
      .content(
        """
          {
            "userRating": "9",
            "watched": true
          }
        """
      ))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value("WE-1"))
      .andExpect(jsonPath("$.imdbId").value("tt1375666"))
      .andExpect(jsonPath("$.userRating").value("9"))
      .andExpect(jsonPath("$.watched").value(true));
  }

  @Test
  void updateEntry_returnsNotFound_whenEntryDoesNotExist() throws Exception {
    when(watchlistEntryService.updateEntry(
      eq("WE-404"),
      any(UpdateWatchlistEntryRequestDto.class)
    )).thenThrow(new WatchlistEntryNotFoundException("WE-404"));

    mockMvc.perform(patch("/api/entries/WE-404")
      .contentType(MediaType.APPLICATION_JSON)
      .content(
        """
          {
            "userRating": "9",
            "watched": true
          }
        """
      ))
      .andExpect(status().isNotFound());
  }
}

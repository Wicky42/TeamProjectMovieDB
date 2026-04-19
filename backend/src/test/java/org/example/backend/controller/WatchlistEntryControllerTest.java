package org.example.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import java.util.List;

@WebMvcTest(WatchlistEntryController.class)
@AutoConfigureMockMvc
public class WatchlistEntryControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private WatchlistEntryService watchlistEntryService;

  private WatchlistEntryDto validWatchlistEntryDto() {
    return new WatchlistEntryDto(
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
  }

  @Test
  void updateEntry_returnsUpdatedWatchlistEntryDto_whenRequestIsValid() throws Exception {
    WatchlistEntryDto response = validWatchlistEntryDto();

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
      .andExpect(jsonPath("$.imdbID").value("tt1375666"))
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

  @Test
  void getEntries_returnsAllEntries_whenWatchedParamIsMissing() throws Exception {
    WatchlistEntryDto dto1 = validWatchlistEntryDto().withWatched(true);
    WatchlistEntryDto dto2 = validWatchlistEntryDto()
      .withWatched(false)
      .withId("WE-2");

    when(watchlistEntryService.findEntries(null)).thenReturn(List.of(dto1, dto2));

    mockMvc.perform(get("/api/entries"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$[0].id").value(dto1.id()))
      .andExpect(jsonPath("$[0].watched").value(dto1.watched()))
      .andExpect(jsonPath("$[1].id").value(dto2.id()))
      .andExpect(jsonPath("$[1].watched").value(dto2.watched()));

    verify(watchlistEntryService).findEntries(null);
    verifyNoMoreInteractions(watchlistEntryService);
  }

  @Test
  void getEntries_returnsWatchedEntries_whenWatchedIsTrue() throws Exception {
    WatchlistEntryDto dto = validWatchlistEntryDto();

    when(watchlistEntryService.findEntries(true)).thenReturn(List.of(dto));

    mockMvc.perform(get("/api/entries").param("watched", "true"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$[0].id").value(dto.id()));

    verify(watchlistEntryService).findEntries(true);
    verifyNoMoreInteractions(watchlistEntryService);
  }

  @Test
  void getEntries_returnsUnwatchedEntries_whenWatchedIsFalse() throws Exception {
    WatchlistEntryDto dto = validWatchlistEntryDto();

    when(watchlistEntryService.findEntries(false)).thenReturn(List.of(dto));

    mockMvc.perform(get("/api/entries").param("watched", "false"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$[0].id").value(dto.id()));

    verify(watchlistEntryService).findEntries(false);
    verifyNoMoreInteractions(watchlistEntryService);
  }
}

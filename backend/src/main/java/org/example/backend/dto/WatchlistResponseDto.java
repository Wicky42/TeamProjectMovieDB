package org.example.backend.dto;

import lombok.With;

import java.util.List;

@With
public record WatchlistResponseDto(
        String id,
        String name,
        List<WatchlistEntryDto> entries,
        String description
) {
}

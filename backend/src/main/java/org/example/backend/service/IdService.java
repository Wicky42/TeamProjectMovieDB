package org.example.backend.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class IdService {
  public String generateWatchlistId() {
    return "W-" + UUID.randomUUID().toString();
  }

  public String generateWatchlistEntryId() {
    return "WE-" + UUID.randomUUID().toString();
  }
}

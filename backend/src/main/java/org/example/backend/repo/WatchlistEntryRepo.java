package org.example.backend.repo;

import java.util.List;
import java.util.Optional;

import org.example.backend.domain.WatchlistEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchlistEntryRepo extends MongoRepository<WatchlistEntry, String> {
  Optional<WatchlistEntry> findByImdbId(String imdbId);
  List<WatchlistEntry> findAllByWatched(Boolean watched);
}

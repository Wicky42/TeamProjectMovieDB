package org.example.backend.repo;

import org.example.backend.domain.Watchlist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchlistRepo extends MongoRepository<Watchlist, String> {

}

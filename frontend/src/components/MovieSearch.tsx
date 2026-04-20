import React, { useEffect, useState } from 'react';
import MovieList from './MovieList';
import MovieSearchInput from './MovieSearchInput';

import type { Movie } from '../types/MovieType';

const MovieSearch: React.FC = () => {
  const [movies, setMovies] = useState<Movie[]>([]);
  const [searchInput, setSearchInput] = useState<string>('');
  const [isLoading, setIsLoading] = useState<boolean>(false);

  useEffect(() => {
    if (searchInput.trim() === '') {
      setMovies([]);
      return;
    }

    const fetchMovies = async () => {
      try {
        setIsLoading(true);
        const response = await fetch(`/api/movies?title=${encodeURIComponent(searchInput)}`);

        if (!response.ok) {
          setMovies([]);
          return;
        }
        const data = await response.json();
        setMovies(data);
      } catch (error) {
        console.error('Error fetching movies:', error);
        setMovies([]);
      } finally {
        setIsLoading(false);
      }
    }

    fetchMovies();
  }, [searchInput]);

  return (
    <>
      <MovieSearchInput
        id="movie-search"
        label="Search for a specific movie or series"
        placeholder="Search for a movie or series..."
        setSearchInput={setSearchInput}
      />

      {isLoading && <p>Loading...</p>}
      {!isLoading && searchInput && movies.length > 0 && <MovieList movies={movies} />}
      {!isLoading && searchInput && movies.length === 0 && <p>No movies found.</p>}
    </>
  );
};

export default MovieSearch;

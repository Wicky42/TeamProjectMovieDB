import React, { useEffect, useState } from 'react';
import MovieList from './MovieList';
import MovieSearchInput from './MovieSearchInput';

import type { Movie } from '../types/MovieType';

const MovieAiRecommendation: React.FC = () => {
  const [movie, setMovie] = useState<Movie>();
  const [searchInput, setSearchInput] = useState<string>('');
  const [isLoading, setIsLoading] = useState<boolean>(false);

  useEffect(() => {
    if (searchInput.trim() === '') {
      setMovie(undefined);
      return;
    }

    const fetchMovies = async () => {
      try {
        setIsLoading(true);

        const response = await fetch('/api/movies/suggestion', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(searchInput),
        });
        if (!response.ok) {
          setIsLoading(false);
          throw new Error('Failed to fetch movies');
        }
        const data = await response.json();
        setMovie(data);
      } catch (error) {
        console.error('Error fetching movies:', error);
        setMovie(undefined);
      } finally {
        setIsLoading(false);
      }
    }

    fetchMovies();
  }, [searchInput]);

  return (
    <>
      <MovieSearchInput
        label="Don't know what to watch? Tell us what you'd like and we recommend a single film or series"
        placeholder="Describe what you'd like to watch..."
        setSearchInput={setSearchInput}
      />

      {isLoading && <p>Loading...</p>}
      {!isLoading && searchInput && movie && <MovieList movies={[movie]} />}
      {!isLoading && searchInput && !movie && <p>No movies found.</p>}
    </>
  );
};

export default MovieAiRecommendation;

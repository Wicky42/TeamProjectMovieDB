import React from 'react';
import MovieCard from './MovieCard';

import type { Movie } from '../types/MovieType';
interface MovieListProps {
  movies: Movie[];
}

const MovieList: React.FC<MovieListProps> = ({ movies }) => {
  return (
    <section
      style={{
        display: 'grid',
        gap: '32px',
        gridTemplateColumns: 'repeat(auto-fit, minmax(340px, 1fr))'
      }}
    >
      {movies.map((movie) => (
        <MovieCard key={movie.imdbId} {...movie} />
      ))}
    </section>
  );
};

export default MovieList;

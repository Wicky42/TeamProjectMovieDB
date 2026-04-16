
import React from 'react';
import '../styles/index.css';
import '../styles/movie-card.css';

interface MovieCardProps {
  title: string;
  poster: string;
  year: string;
  metascore: string;
  imdbRating: string;
  plot: string;
}

const MovieCard: React.FC<MovieCardProps> = ({ title, poster, year, metascore, imdbRating, plot }) => {
  return (
    <article className="movie-card">
      <div className="movie-card__poster-wrapper">
        <img className="movie-card__poster" src={poster} alt={title} />
      </div>
      <div className="movie-card__content">
        <div className="movie-card__header">
          <h2 className="movie-card__title">{title}</h2>
          <span className="movie-card__release-year">{year}</span>
        </div>
        <div className="badge" style={{marginTop: '.5rem'}}>IMDb: {imdbRating} | Metascore: {metascore}</div>
        <p className="movie-card__overview">{plot.substring(0, 100)}...</p>
      </div>
        <button>Add to Watchlist</button>
    </article>
  );
};

export default MovieCard;

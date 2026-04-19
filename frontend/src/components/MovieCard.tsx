import React, { useEffect, useState } from "react";
import "../styles/index.css";
import "../styles/movie-card.css";
import type { WatchlistResponse } from "../types/Watchlist";

interface MovieCardProps {
  imdbID: string;
  title: string;
  poster: string;
  year: string;
  metascore: string;
  imdbRating: string;
  plot: string;
}

const MovieCard: React.FC<MovieCardProps> = ({
                                               imdbID,
                                               title,
                                               poster,
                                               year,
                                               metascore,
                                               imdbRating,
                                               plot,
                                             }) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [watchlists, setWatchlists] = useState<WatchlistResponse[]>([]);
  const [isLoadingWatchlists, setIsLoadingWatchlists] = useState(false);
  const [isAdding, setIsAdding] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    if (!isModalOpen) return;

    const fetchWatchlists = async () => {
      try {
        setIsLoadingWatchlists(true);
        setError("");

        const response = await fetch("/api/watchlists");

        if (!response.ok) {
          setError("Could not load watchlists.");
          return;
        }

        const data = await response.json();

        setWatchlists(Array.isArray(data) ? data : []);
      } catch (err) {
        console.error(err);
        setError("Could not load watchlists.");
      } finally {
        setIsLoadingWatchlists(false);
      }
    };

    fetchWatchlists();
  }, [isModalOpen]);

  const handleAddToWatchlist = async (watchlistId: string) => {
    if (!imdbID) {
      setError("No imdbID found for this movie.");
      return;
    }

    try {
      setIsAdding(true);
      setError("");
      setSuccess("");

      const response = await fetch(`/api/watchlists/${watchlistId}/entries`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          imdbID,
        }),
      });

      if (!response.ok) {
        setError("Could not add movie to watchlist.");
        return;
      }

      await response.json();

      setSuccess(`"${title}" was added successfully.`);
      setTimeout(() => {
        setIsModalOpen(false);
        setSuccess("");
      }, 800);
    } catch (err) {
      console.error(err);
      setError("Could not add movie to watchlist.");
    } finally {
      setIsAdding(false);
    }
  };

  return (
      <>
        <article className="movie-card">
          <div className="movie-card__poster-wrapper">
            <img className="movie-card__poster" src={poster} alt={title} />
          </div>

          <div className="movie-card__content">
            <div className="movie-card__header">
              <h2 className="movie-card__title">{title}</h2>
              <span className="movie-card__release-year">{year}</span>
            </div>

            <div className="badge" style={{ marginTop: ".5rem" }}>
              IMDb: {imdbRating} | Metascore: {metascore}
            </div>

            <p className="movie-card__overview">{plot.substring(0, 100)}...</p>
          </div>

          <button
              className="movie-card__watchlist-button"
              onClick={() => setIsModalOpen(true)}
          >
            Add to Watchlist
          </button>
        </article>

        {isModalOpen && (
            <div
                className="watchlist-modal__backdrop"
                onClick={() => setIsModalOpen(false)}
                onKeyDown={(e) => {
                  if (e.key === "Enter" || e.key === "Escape" || e.key === " ") {
                    setIsModalOpen(false);
                  }
                }}
            >
              <div
                  className="watchlist-modal"
                  onClick={(event) => event.stopPropagation()}
              >
                <div className="watchlist-modal__header">
                  <h3>Select a Watchlist</h3>
                  <button
                      className="watchlist-modal__close"
                      onClick={() => setIsModalOpen(false)}
                  >
                    ✕
                  </button>
                </div>

                {isLoadingWatchlists && (
                    <p className="watchlist-modal__state">Loading watchlists...</p>
                )}

                {error && <p className="watchlist-modal__state error">{error}</p>}
                {success && <p className="watchlist-modal__state success">{success}</p>}

                {!isLoadingWatchlists && !error && watchlists.length === 0 && (
                    <p className="watchlist-modal__state">No watchlists available.</p>
                )}

                {!isLoadingWatchlists && watchlists.length > 0 && (
                    <div className="watchlist-modal__list">
                      {watchlists.map((watchlist) => (
                          <button
                              key={watchlist.id}
                              className="watchlist-modal__item"
                              onClick={() => handleAddToWatchlist(watchlist.id)}
                              disabled={isAdding}
                          >
                            <div className="watchlist-modal__item-content">
                      <span className="watchlist-modal__item-title">
                        {watchlist.name}
                      </span>
                              <span className="watchlist-modal__item-count">
                        {watchlist.entries?.length ?? 0} entries
                      </span>
                            </div>
                          </button>
                      ))}
                    </div>
                )}
              </div>
            </div>
        )}
      </>
  );
};

export default MovieCard;


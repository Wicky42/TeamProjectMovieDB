import { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import type { WatchlistEntry, WatchlistResponse } from "../types/Watchlist";
import "./WatchlistDetailPage.css";
import {FormControlLabel, FormGroup, Rating, Switch} from "@mui/material";
import StarIcon from '@mui/icons-material/Star';

type WatchlistDetailLocationState = {
  watchlistData?: WatchlistResponse;
};


export default function WatchlistDetailPage() {
    // const { watchlistId } = useParams();
    const navigate = useNavigate();
    const location = useLocation();

    const { watchlistData } = (location.state as WatchlistDetailLocationState) || {};

    const [watchlist, setWatchlist] = useState<WatchlistResponse | null>(watchlistData ||null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
      console.log("USE EFF watchlistData: ", watchlistData);
        const fetchWatchlist = async () => {
          try {
            setIsLoading(true);
            setError("");

            console.log("location.pathname", location.pathname, location.pathname.includes("watched"));
            const watched = location.pathname === ("/watchlists/watched") ? true : false;
            console.log("WATCHED? ", watched)
            const response = await fetch(`/api/entries?watched=${watched}`);

            if (!response.ok) {
                setError(`Could not load titles. Status: ${response.status}`);
                return;
            }

            const data: WatchlistEntry[] = await response.json();
            createAndSetWatchlist(data, watched);
          } catch (err) {
            console.error(err);
            setError("Could not load watchlist details.");
          } finally {
            setIsLoading(false);
          }
        };

        if (!watchlistData) fetchWatchlist();
        else setIsLoading(false);
    }, [watchlistData, location.pathname]);

    const createAndSetWatchlist = (entries: WatchlistEntry[], watched: boolean) => {
        const newWatchlist: WatchlistResponse = {
            id: watched ? "W-watched" : "W-unwatched",
            name: watched ? "Watched Titles" : "Unwatched Titles",
            entries,
            description: watched ? "All the movies and series you've marked as watched across all your watchlists." : "All the movies and series you've marked as unwatched across all your watchlists."
        };
        setWatchlist(newWatchlist);
    };

    const handleWatchedChange = async (entryId: string, watched: boolean) => {
        try {
            const response = await fetch(`/api/entries/${entryId}`, {
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ watched }),
            });

            if (!response.ok) {
                console.error("Failed to update watched status");
                return;
            }

            setWatchlist((prev) => {
                if (!prev) return prev;
                return {
                    ...prev,
                    entries: prev.entries.map((entry) =>
                        entry.id === entryId ? { ...entry, watched } : entry
                    ),
                };
            });
        } catch (err) {
            console.error("Failed to update watched status", err);
        }
    };

    const handleEntryRating = async (entryId: string, newValue: number | null) => {
        const userRating = newValue !== null ? String(newValue) : "0";
        try {
            const response = await fetch(`/api/entries/${entryId}`, {
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ userRating }),
            });

            if (!response.ok) {
                console.error("Failed to update rating");
                return;
            }

            setWatchlist((prev) => {
                if (!prev) return prev;
                return {
                    ...prev,
                    entries: prev.entries.map((entry) =>
                        entry.id === entryId ? { ...entry, userRating } : entry
                    ),
                };
            });
        } catch (err) {
            console.error("Failed to update rating", err);
        }
    };

    if (isLoading) {
        return <p className="watchlist-detail-page__state">Loading watchlist...</p>;
    }

    if (error) {
        return (
            <p className="watchlist-detail-page__state watchlist-detail-page__state--error">
                {error}
            </p>
        );
    }

    if (!watchlist) {
        return <p className="watchlist-detail-page__state">Watchlist not found.</p>;
    }

    return (
        <>
        <section className="watchlist-detail-page">
            <header className="watchlist-detail-page__header">
                <div className="watchlist-detail-page__title-wrapper">
                    <h1 className="watchlist-detail-page__title">{watchlist.name}</h1>
                </div>
                <p className="watchlist-detail-page__description">
                    {watchlist.description || "No description available."}
                </p>
                <p className="watchlist-detail-page__count">
                    {watchlist.entries.length} movies
                </p>
            </header>

            {watchlist.entries.length === 0 ? (
                <p className="watchlist-detail-page__state">
                    No movies in this watchlist yet.
                </p>
            ) : (
                <div className="watchlist-detail-grid">
                    {watchlist.entries.map((entry) => (
                        <article key={entry.id} className="watchlist-entry-card">
                            <img
                                className="watchlist-entry-card__poster"
                                src={entry.poster}
                                alt={entry.title}
                            />

                            <div className="watchlist-entry-card__content">
                                <div className="watchlist-entry-card__top">
                                    <div className="watchlist-entry-card__heading-group">
                                        <h2 className="watchlist-entry-card__title">{entry.title}</h2>
                                        <span className="watchlist-entry-card__year">{entry.year}</span>
                                    </div>
                                </div>

                                <p className="watchlist-entry-card__meta">
                                    IMDb: {entry.imdbRating} | Metascore: {entry.metascore}
                                </p>

                                <p className="watchlist-entry-card__genre">
                                    {entry.genre || "No genre available"}
                                </p>

                                <p className="watchlist-entry-card__plot">
                                    {entry.plot
                                        ? `${entry.plot.slice(0, 160)}...`
                                        : "No plot available."}
                                </p>

                                <div className="watchlist-entry-card__user-actions">
                                    <FormGroup>
                                        <FormControlLabel
                                            control={<Switch checked={entry.watched} onChange={(e) => handleWatchedChange(entry.id, e.target.checked)} />}
                                            label="Watched"
                                        />
                                    </FormGroup>
                                    <Rating
                                        name={`rating-${entry.id}`}
                                        value={parseFloat(entry.userRating) || 0}
                                        precision={0.5}
                                        onChange={(_event, newValue) => handleEntryRating(entry.id, newValue)}
                                        emptyIcon={
                                            <StarIcon sx={{ color: 'transparent', stroke: 'white', strokeWidth: 1.5 }} />
                                        }
                                    />
                                </div>
                            </div>
                        </article>
                    ))}
                </div>
            )}
        </section>
    </>
    );
}
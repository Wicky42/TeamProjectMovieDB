import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import type { WatchlistResponse } from "../types/Watchlist";
import "./WatchlistsPage.css";

export default function WatchlistsPage() {
    const [watchlists, setWatchlists] = useState<WatchlistResponse[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        const fetchWatchlists = async () => {
            try {
                setIsLoading(true);
                setError("");

                const response = await fetch(`/api/watchlists`);
                if (!response.ok) {
                    const body = await response.text();
                    setError(`Could not load watchlists. ${response.status} ${response.statusText}${body ? ` - ${body}` : ""}`);
                    return;
                }

                const data: WatchlistResponse[] = await response.json();
                setWatchlists(data);
            } catch (err) {
                console.error(err);
                setError("Could not load watchlists.");
            } finally {
                setIsLoading(false);
            }
        };

        fetchWatchlists();
    }, []);

    return (
        <section className="watchlists-page">
            <h2 className="watchlists-page__title">Your Watchlists</h2>

            {isLoading && <p>Loading watchlists...</p>}
            {error && <p className="watchlists-page__state--error">{error}</p>}

            {!isLoading && !error && watchlists.length === 0 && (
                <p>No watchlists found yet.</p>
            )}

            {!isLoading && !error && watchlists.length > 0 && (
                <div className="watchlists-grid">
                    {watchlists.map((watchlist) => (
                        <Link
                            key={watchlist.id}
                            to={`/watchlists/${watchlist.id}`}
                            className="watchlist-card__link"
                        >
                            <article className="watchlist-card">
                                <div className="watchlist-card__top">
                                    <h3 className="watchlist-card__title">{watchlist.name}</h3>
                                    <span className="watchlist-card__count">
                    {watchlist.entries?.length ?? 0} movies
                  </span>
                                </div>

                                <p className="watchlist-card__description">
                                    {watchlist.description || "No description available."}
                                </p>

                                <div className="watchlist-card__preview">
                                    {watchlist.entries.slice(0, 3).length > 0 ? (
                                        <div className="watchlist-card__poster-list">
                                            {watchlist.entries.slice(0, 3).map((entry) => (
                                                <div key={entry.id} className="watchlist-card__poster-frame">
                                                    {entry.poster && entry.poster !== "N/A" ? (
                                                        <img
                                                            src={entry.poster}
                                                            alt={entry.title}
                                                            className="watchlist-card__poster"
                                                        />
                                                    ) : (
                                                        <div className="watchlist-card__poster watchlist-card__poster--fallback">
                                                            No poster
                                                        </div>
                                                    )}
                                                </div>
                                            ))}
                                        </div>
                                    ) : (
                                        <p className="watchlist-card__preview-empty">
                                            No movie preview available yet.
                                        </p>
                                    )}
                                </div>
                            </article>
                        </Link>
                    ))}
                </div>
            )}
        </section>
    );
}
import { useEffect, useState } from "react";
import "./WatchlistsPage.css";

type Watchlist = {
    id: string;
    name: string;
    watchlistEntryIds: string[];
    description: string;
};

export default function WatchlistsPage() {
    const [watchlists, setWatchlists] = useState<Watchlist[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        const fetchWatchlists = async () => {
            try {
                setIsLoading(true);
                setError("");

                const response = await fetch("/api/watchlists");

                if (!response.ok) {
                    throw new Error("Failed to fetch watchlists");
                }

                const data: Watchlist[] = await response.json();
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
            <div className="watchlists-page__header">
                <h2 className="watchlists-page__title">Your Watchlists</h2>
                <p className="watchlists-page__subtitle">
                    Here you can find all your created watchlists.
                </p>
            </div>

            {isLoading && <p className="watchlists-page__state">Loading watchlists...</p>}
            {error && <p className="watchlists-page__state watchlists-page__state--error">{error}</p>}

            {!isLoading && !error && watchlists.length === 0 && (
                <p className="watchlists-page__state">No watchlists found yet.</p>
            )}

            {!isLoading && !error && watchlists.length > 0 && (
                <div className="watchlists-grid">
                    {watchlists.map((watchlist) => (
                        <article key={watchlist.id} className="watchlist-card">
                            <div className="watchlist-card__top">
                                <h3 className="watchlist-card__title">{watchlist.name}</h3>
                                <span className="watchlist-card__count">
                  {watchlist.watchlistEntryIds.length} movies
                </span>
                            </div>

                            <p className="watchlist-card__description">
                                {watchlist.description || "No description available."}
                            </p>
                        </article>
                    ))}
                </div>
            )}
        </section>
    );
}
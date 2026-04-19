import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import type { WatchlistResponse } from "../types/Watchlist";
import "./WatchlistDetailPage.css";

export default function WatchlistDetailPage() {
    const { watchlistId } = useParams();
    const navigate = useNavigate();

    const [watchlist, setWatchlist] = useState<WatchlistResponse | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");
    const [deleteError, setDeleteError] = useState("");
    const [deletingEntryId, setDeletingEntryId] = useState<string | null>(null);
    const [isDeletingWatchlist, setIsDeletingWatchlist] = useState(false);
    const [isDeleteConfirmOpen, setIsDeleteConfirmOpen] = useState(false);

    useEffect(() => {
        const fetchWatchlist = async () => {
            if (!watchlistId) {
                setError("No watchlist id provided.");
                setIsLoading(false);
                return;
            }

            try {
                setIsLoading(true);
                setError("");

                const response = await fetch(`/api/watchlists/${watchlistId}`);

                if (!response.ok) {
                    setError(`Could not load watchlist details. Status: ${response.status}`);
                    return;
                }

                const data: WatchlistResponse = await response.json();
                setWatchlist(data);
            } catch (err) {
                console.error(err);
                setError("Could not load watchlist details.");
            } finally {
                setIsLoading(false);
            }
        };

        fetchWatchlist();
    }, [watchlistId]);

    const handleRemoveEntry = async (entryId: string) => {
        if (!watchlistId) {
            setDeleteError("No watchlist id provided.");
            return;
        }

        try {
            setDeletingEntryId(entryId);
            setDeleteError("");

            const response = await fetch(`/api/watchlists/${watchlistId}/entries/${entryId}`, {
                method: "DELETE",
            });

            if (!response.ok) {
                const body = await response.text();
                setDeleteError(body || "Movie could not be removed from the watchlist.");
                return;
            }

            setWatchlist((currentWatchlist) => {
                if (!currentWatchlist) {
                    return currentWatchlist;
                }

                return {
                    ...currentWatchlist,
                    entries: currentWatchlist.entries.filter((entry) => entry.id !== entryId),
                };
            });
        } catch (err) {
            console.error(err);
            setDeleteError("Movie could not be removed from the watchlist.");
        } finally {
            setDeletingEntryId(null);
        }
    };

    const handleDeleteWatchlist = async () => {
        if (!watchlistId) {
            setDeleteError("No watchlist id provided.");
            return;
        }

        setIsDeleteConfirmOpen(true);
    };

    const handleConfirmDelete = async () => {
        if (!watchlistId) {
            setDeleteError("No watchlist id provided.");
            return;
        }

        setIsDeleteConfirmOpen(false);

        try {
            setIsDeletingWatchlist(true);
            setDeleteError("");

            const response = await fetch(`/api/watchlists/${watchlistId}`, {
                method: "DELETE",
            });

            if (!response.ok) {
                const body = await response.text();
                setDeleteError(body || "Watchlist could not be deleted.");
                return;
            }

            navigate("/watchlists");
        } catch (err) {
            console.error(err);
            setDeleteError("Watchlist could not be deleted.");
        } finally {
            setIsDeletingWatchlist(false);
        }
    };

    const handleCancelDelete = () => {
        setIsDeleteConfirmOpen(false);
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
                    <button
                        type="button"
                        className="watchlist-detail-page__delete-button"
                        onClick={handleDeleteWatchlist}
                        disabled={isDeletingWatchlist}
                        title="Delete this watchlist"
                    >
                        {isDeletingWatchlist ? "Deleting..." : "Delete Watchlist"}
                    </button>
                </div>
                <p className="watchlist-detail-page__description">
                    {watchlist.description || "No description available."}
                </p>
                <p className="watchlist-detail-page__count">
                    {watchlist.entries.length} movies
                </p>
                {deleteError && (
                    <p className="watchlist-detail-page__state watchlist-detail-page__state--error">
                        {deleteError}
                    </p>
                )}
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

                                    <button
                                        type="button"
                                        className="watchlist-entry-card__delete-button"
                                        onClick={() => handleRemoveEntry(entry.id)}
                                        disabled={deletingEntryId === entry.id}
                                    >
                                        {deletingEntryId === entry.id ? "Deleting movie..." : "Remove Movie"}
                                    </button>
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
                            </div>
                        </article>
                    ))}
                </div>
            )}
        </section>

        {isDeleteConfirmOpen && (
            <div className="watchlist-delete-modal__backdrop" onClick={handleCancelDelete}>
                <div
                    className="watchlist-delete-modal"
                    onClick={(e) => e.stopPropagation()}
                >
                    <h2 className="watchlist-delete-modal__title">Delete Watchlist</h2>
                    <p className="watchlist-delete-modal__message">
                        Are you sure you want to permanently delete this watchlist? This cannot be undone.
                    </p>

                    <div className="watchlist-delete-modal__actions">
                        <button
                            type="button"
                            className="watchlist-delete-modal__cancel-button"
                            onClick={handleCancelDelete}
                            disabled={isDeletingWatchlist}
                        >
                            Cancel
                        </button>
                        <button
                            type="button"
                            className="watchlist-delete-modal__delete-button"
                            onClick={handleConfirmDelete}
                            disabled={isDeletingWatchlist}
                        >
                            {isDeletingWatchlist ? "Deleting..." : "Delete"}
                        </button>
                    </div>
                </div>
            </div>
        )}
    </>
    );
}
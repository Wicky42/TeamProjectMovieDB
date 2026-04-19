import { useState } from "react";
import "./CreateWatchlistPage.css";

type CreateWatchlistRequest = {
    name: string;
    description: string;
};

export default function CreateWatchlistPage() {
    const [formData, setFormData] = useState<CreateWatchlistRequest>({
        name: "",
        description: "",
    });

    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    const handleChange = (
        event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
    ) => {
        const { name, value } = event.target;

        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setError("");
        setSuccess("");

        if (!formData.name.trim()) {
            setError("Please enter a watchlist name.");
            return;
        }

        try {
            setIsLoading(true);

            const response = await fetch("/api/watchlists", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    name: formData.name.trim(),
                    description: formData.description.trim(),
                }),
            });

            if (!response.ok) {
                throw new Error(`Failed to create watchlist. Status: ${response.status}`);
            }

            await response.json();

            setSuccess("Watchlist created successfully.");
            setFormData({
                name: "",
                description: "",
            });
        } catch (err) {
            console.error(err);
            setError("Something went wrong while creating the watchlist.");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <section className="create-watchlist-page">
            <div className="create-watchlist-card">
                <h2 className="create-watchlist-card__title">Create a new Watchlist</h2>

                <form className="create-watchlist-form" onSubmit={handleSubmit}>
                    <div className="create-watchlist-form__group">
                        <label htmlFor="name" className="create-watchlist-form__label">
                            Name
                        </label>
                        <input
                            id="name"
                            name="name"
                            type="text"
                            value={formData.name}
                            onChange={handleChange}
                            placeholder="e.g. Sci-Fi Favorites"
                            className="create-watchlist-form__input"
                        />
                    </div>

                    <div className="create-watchlist-form__group">
                        <label
                            htmlFor="description"
                            className="create-watchlist-form__label"
                        >
                            Description
                        </label>
                        <textarea
                            id="description"
                            name="description"
                            value={formData.description}
                            onChange={handleChange}
                            placeholder="Describe your watchlist..."
                            rows={5}
                            className="create-watchlist-form__textarea"
                        />
                    </div>

                    {error && <p className="create-watchlist-form__message error">{error}</p>}
                    {success && (
                        <p className="create-watchlist-form__message success">{success}</p>
                    )}

                    <button
                        type="submit"
                        className="create-watchlist-form__button"
                        disabled={isLoading}
                    >
                        {isLoading ? "Creating..." : "Create"}
                    </button>
                </form>
            </div>
        </section>
    );
}
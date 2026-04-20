export type WatchlistEntry = {
    id: string;
    imdbID: string;
    userRating: string;
    watched: boolean;
    title: string;
    poster: string;
    year: string;
    type: string;
    genre: string;
    metascore: string;
    imdbRating: string;
    plot: string;
};

export type WatchlistResponse = {
    id: string;
    name: string;
    entries: WatchlistEntry[];
    description: string;
};
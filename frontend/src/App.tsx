import './styles/index.css';

import Header from './components/Header';
import Footer from './components/Footer';
import MovieSearch from './components/MovieSearch';
import MovieAiRecommendation from './components/MovieAiRecommendation';
import { Routes, Route } from "react-router-dom";
import CreateWatchlistPage from "./pages/CreateWatchlistPage";
import WatchlistsPage from "./pages/WatchlistsPage";
import WatchlistDetailPage from "./pages/WatchlistDetailPage";
import WatchedAndUnwatchedDetailsPage from "./pages/WatchedAndUnwatchedDetailsPage";

function App() {
  return (
    <div className="app-shell">
      <Header />
      <main className="app-shell__content">
          <Routes>
              <Route
                  path="/"
                  element={
                      <>
                          <MovieSearch />
                          <MovieAiRecommendation />
                      </>
                  }
              />

              <Route
                  path="/create-watchlist"
                  element={<CreateWatchlistPage />}
              />
              <Route path="/watchlists/:watchlistId" element={<WatchlistDetailPage />} />
              <Route path="/watchlists/watched" element={<WatchedAndUnwatchedDetailsPage />} />
              <Route path="/watchlists/unwatched" element={<WatchedAndUnwatchedDetailsPage />} />
              <Route path="/watchlists" element={<WatchlistsPage />} />
          </Routes>
      </main>
      <Footer />
    </div>
  );
}

export default App;

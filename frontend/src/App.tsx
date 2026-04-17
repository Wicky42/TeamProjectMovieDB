import './styles/index.css';

import Header from './components/Header';
import Footer from './components/Footer';
import MovieSearch from './components/MovieSearch';
import MovieAiRecommendation from './components/MovieAiRecommendation';
import { Routes, Route } from "react-router-dom";
import CreateWatchlistPage from "./pages/CreateWatchlistPage.tsx";

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
          </Routes>
      </main>
      <Footer />
    </div>
  );
}

export default App;

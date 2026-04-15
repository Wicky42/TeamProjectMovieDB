import './styles/index.css';

import Header from './components/Header';
import Footer from './components/Footer';
import MovieSearch from './components/MovieSearch';

function App() {
  return (
    <div className="app-shell">
      <Header />
      <main className="app-shell__content">
        <MovieSearch />
      </main>
      <Footer />
    </div>
  );
}

export default App;

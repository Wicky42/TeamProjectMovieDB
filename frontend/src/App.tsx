import './styles/index.css';

import Header from './components/Header';
import Footer from './components/Footer';

function App() {
  return (
    <div className="app-shell">
      <Header />
      <main className="app-shell__content">
      </main>
      <Footer />
    </div>
  );
}

export default App;

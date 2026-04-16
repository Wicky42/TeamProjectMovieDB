
import watchyLogo from '../assets/watchy_logo.png';

const Header: React.FC = () => (
  <header
    className="app-shell__header"
    style={{
      padding: '2rem 2rem',
      background: 'linear-gradient(90deg, #10182a 80%, #152040 100%)',
      borderBottom: '1.5px solid #4dafff',
      marginBottom: '32px',
      boxShadow: '0 4px 24px 0 rgba(77,175,255,0.07)'
    }}
  >
    <div className="brand" style={{display: 'flex', alignItems: 'center', fontWeight: 700, fontSize: '2rem', color: 'var(--accent)'}}>
      <img src={watchyLogo} alt="Watchy Logo" style={{height: 'auto', width: 250, marginRight: 12, objectFit: 'contain'}} />
    </div>
    <nav className="app-shell__nav">
      {/* Add navigation links here */}
    </nav>
  </header>
);

export default Header;

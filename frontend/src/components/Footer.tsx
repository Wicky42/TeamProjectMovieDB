
import watchyLogo from '../assets/watchy_logo.png';

const Footer: React.FC = () => (
  <footer
    style={{
      padding: '32px 0',
      background: 'linear-gradient(90deg, #10182a 80%, #152040 100%)',
      borderTop: '1.5px solid #4dafff',
      marginTop: '48px',
      color: 'var(--text-secondary)',
      textAlign: 'center',
      boxShadow: '0 -4px 24px 0 rgba(77,175,255,0.07)'
    }}
  >
    <div style={{display: 'inline-flex', alignItems: 'center'}}>
      <img src={watchyLogo} alt="Watchy Logo" style={{height: 'auto', width: 250, objectFit: 'contain', verticalAlign: 'middle'}} />
    </div>
    <div style={{marginTop: '1rem'}}>
      &copy; {new Date().getFullYear()} WatchY. All rights reserved.
    </div>
  </footer>
);

export default Footer;

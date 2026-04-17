import watchyLogo from '../assets/watchy_logo.png';
import { Link } from "react-router-dom";
import "./Header.css";

const Header: React.FC = () => (
    <header className="header">
        <div className="header__brand">
            <img src={watchyLogo} alt="Watchy Logo" className="header__logo" />
        </div>

        <nav className="header__nav">
            <Link to="/" className="header__link">
                Home
            </Link>

            <Link to="/create-watchlist" className="header__cta">
                Create Watchlist
            </Link>
        </nav>
    </header>
);

export default Header;
import React, { useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

function Layout({ children }) {
    const { user, logout } = useContext(AuthContext);
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <div style={{ display: 'flex', minHeight: '100vh', backgroundColor: '#121212', color: 'white', margin: 0, fontFamily: 'sans-serif' }}>

            {/* SIDEBAR */}
            <div style={{ width: '250px', backgroundColor: '#1e1e1e', padding: '20px', borderRight: '1px solid #333' }}>
                <h2 style={{ color: '#00d8ff', marginTop: 0 }}>CineFiles</h2>
                <ul style={{ listStyleType: 'none', padding: 0, marginTop: '40px' }}>

                    {/* The new Feed Page! */}
                    <li onClick={() => navigate('/feed')} style={{ padding: '15px 10px', cursor: 'pointer', borderBottom: '1px solid #333' }}>🌍 Movie Feed</li>

                    {/* Kept your Dashboard for searching movies */}
                    <li onClick={() => navigate('/dashboard')} style={{ padding: '15px 10px', cursor: 'pointer', borderBottom: '1px solid #333' }}>🎬 Movie Database</li>

                    {/* Wired up Watchlist */}
                    <li onClick={() => navigate('/watchlist')} style={{ padding: '15px 10px', cursor: 'pointer', borderBottom: '1px solid #333' }}>⭐ Watchlist</li>

                    <li onClick={() => navigate('/bonds')} style={{ padding: '15px 10px', cursor: 'pointer', borderBottom: '1px solid #333' }}>💸 Bond Market</li>

                    {/* Placeholder for later */}
                    <li style={{ padding: '15px 10px', cursor: 'pointer', borderBottom: '1px solid #333' }}>⚙️ Settings</li>
                </ul>
            </div>

            {/* MAIN CONTENT WRAPPER */}
            <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>

                {/* TOP NAVBAR */}
                <div style={{ height: '70px', backgroundColor: '#1e1e1e', display: 'flex', alignItems: 'center', justifyContent: 'flex-end', padding: '0 30px', borderBottom: '1px solid #333' }}>
                    <span style={{ marginRight: '20px' }}>User: <strong>{user?.username}</strong></span>
                    <button
                        onClick={handleLogout}
                        style={{ padding: '8px 16px', cursor: 'pointer', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', fontWeight: 'bold' }}>
                        Log Out
                    </button>
                </div>

                {/* ACTUAL PAGE CONTENT */}
                <div style={{ padding: '40px' }}>
                    {children}
                </div>

            </div>
        </div>
    );
}

export default Layout;
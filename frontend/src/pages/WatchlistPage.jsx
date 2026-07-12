import React, { useContext, useEffect, useState } from 'react';
import { AuthContext } from '../context/AuthContext';
import Layout from '../components/Layout';
import { getWatchlist } from '../services/api';

function WatchlistPage() {
    const { user } = useContext(AuthContext);
    const [movies, setMovies] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchMovies = async () => {
            try {
                const data = await getWatchlist(user.username);
                setMovies(data);
            } catch (err) {
                setError(err.error || "Could not load watchlist.");
            } finally {
                setLoading(false);
            }
        };

        if (user) {
            fetchMovies();
        }
    }, [user]);

    if (!user) return (
        <div style={{ padding: '50px', color: 'white', backgroundColor: '#121212', height: '100vh' }}>
            <h2>Access Denied.</h2>
            <a href="/login" style={{ color: '#00d8ff' }}>Go to Login</a>
        </div>
    );

    return (
        <Layout>
            <h1 style={{ marginTop: 0 }}>My Watchlist</h1>

            {loading && <p style={{ color: '#00d8ff' }}>Loading your movies...</p>}
            {error && <p style={{ color: '#ff4d4d' }}>{error}</p>}

            {!loading && movies.length === 0 && !error && (
                <p style={{ color: '#aaa' }}>Your watchlist is empty. Go search for some movies!</p>
            )}

            {/* THE MOVIE GRID */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))', gap: '20px', marginTop: '20px' }}>
                {movies.map((movie) => (
                    <div key={movie.id} style={{ backgroundColor: '#1e1e1e', padding: '20px', borderRadius: '8px', border: '1px solid #333' }}>
                        <h3 style={{ margin: '0 0 10px 0', color: '#fff' }}>{movie.title}</h3>
                        <p style={{ color: '#aaa', margin: '0 0 5px 0', fontSize: '14px' }}><strong>Genre:</strong> {movie.genre || "N/A"}</p>
                        <p style={{ color: '#aaa', margin: '0', fontSize: '14px' }}><strong>Rating:</strong> {movie.rating ? `${movie.rating} / 10` : "N/A"}</p>
                    </div>
                ))}
            </div>
        </Layout>
    );
}

export default WatchlistPage;
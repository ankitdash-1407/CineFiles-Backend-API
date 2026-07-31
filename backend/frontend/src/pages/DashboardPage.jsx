import React, { useContext, useState } from 'react';
import { AuthContext } from '../context/AuthContext';
import Layout from '../components/Layout';
import { searchMovie, addToWatchlist } from '../services/api';

function DashboardPage() {
    // THE FIX: Extracted token from AuthContext
    const { user, token } = useContext(AuthContext);

    // States for our Search UI
    const [searchTerm, setSearchTerm] = useState('');
    const [movie, setMovie] = useState(null);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState('');

    if (!user) {
        return (
            <div style={{ padding: '50px', color: 'white', backgroundColor: '#121212', height: '100vh' }}>
                <h2>Access Denied.</h2>
                <a href="/login" style={{ color: '#00d8ff' }}>Go to Login</a>
            </div>
        );
    }

    const handleSearch = async (e) => {
        e.preventDefault();
        if (!searchTerm) return;

        setLoading(true);
        setMessage('');
        setMovie(null);

        try {
            // THE FIX: Passed the token to the API call
            const data = await searchMovie(searchTerm, token);
            setMovie(data);
        } catch (err) {
            setMessage(err.error || "Movie not found.");
        } finally {
            setLoading(false);
        }
    };

    const handleWatchlist = async () => {
        try {
            // THE FIX: Passed the token to the API call
            const response = await addToWatchlist(user.username, movie.title, token);
            setMessage(response.message || "Added to watchlist!");
        } catch (err) {
            setMessage(err.error || "Failed to add to watchlist.");
        }
    };

    return (
        <Layout>
            <h1 style={{ marginTop: 0 }}>Movie Database</h1>

            {/* THE SEARCH BAR */}
            <form onSubmit={handleSearch} style={{ display: 'flex', gap: '10px', marginBottom: '30px' }}>
                <input
                    type="text"
                    placeholder="Search for a movie (e.g., Inception, The Matrix)..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    style={{ flex: 1, padding: '10px', borderRadius: '4px', border: '1px solid #444', backgroundColor: '#222', color: 'white' }}
                />
                <button type="submit" disabled={loading} style={{ padding: '10px 20px', cursor: 'pointer', backgroundColor: '#00d8ff', color: 'black', border: 'none', borderRadius: '4px', fontWeight: 'bold' }}>
                    {loading ? 'Searching...' : 'Search'}
                </button>
            </form>

            {/* SYSTEM MESSAGES (Errors or Success) */}
            {message && <p style={{ color: message.includes('error') || message.includes('failed') ? '#ff4d4d' : '#00d8ff', fontWeight: 'bold' }}>{message}</p>}

            {/* THE MOVIE DISPLAY CARD */}
            {movie && (
                <div style={{ backgroundColor: '#1e1e1e', padding: '20px', borderRadius: '8px', border: '1px solid #333' }}>

                    {/* MOVIE DETAILS */}
                    <div>
                        <h2 style={{ margin: '0 0 10px 0', color: '#fff' }}>{movie.title}</h2>
                        <p style={{ color: '#aaa', margin: '0 0 5px 0' }}><strong>Genre:</strong> {movie.genre || "N/A"}</p>
                        <p style={{ color: '#aaa', margin: '0 0 15px 0' }}><strong>Rating:</strong> {movie.rating ? `${movie.rating} / 10` : "N/A"}</p>
                    </div>

                    <button
                        onClick={handleWatchlist}
                        style={{ padding: '10px 20px', cursor: 'pointer', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: '4px', fontWeight: 'bold' }}>
                        ➕ Add to Watchlist
                    </button>

                </div>
            )}
        </Layout>
    );
}

export default DashboardPage;
import React, { useState, useEffect, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';

const CreatePost = ({ currentUserId }) => {
    const { token } = useContext(AuthContext);

    // Auto-complete States
    const [movieId, setMovieId] = useState("");
    const [movieTitle, setMovieTitle] = useState("");
    const [searchResults, setSearchResults] = useState([]);
    const [showDropdown, setShowDropdown] = useState(false);

    // Post States
    const [text, setText] = useState("");
    const [isCampaign, setIsCampaign] = useState(false);
    const [fundingTarget, setFundingTarget] = useState("");

    // Debounced Search Engine
    useEffect(() => {
        if (movieTitle.length < 2) {
            setSearchResults([]);
            setShowDropdown(false);
            return;
        }

        const delayDebounceFn = setTimeout(async () => {
            try {
                // FIX: Removed AWS URL, starts with /api now
                const response = await fetch(`/api/movies/autocomplete?query=${movieTitle}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    }
                });

                if (response.ok) {
                    const data = await response.json();
                    setSearchResults(data);
                    setShowDropdown(true);
                }
            } catch (error) {
                console.error("Movie search failed:", error);
            }
        }, 500);

        return () => clearTimeout(delayDebounceFn);
    }, [movieTitle, token]);

    // When the user clicks a movie in the dropdown
    const handleSelectMovie = (id, title) => {
        setMovieId(id);
        setMovieTitle(title);
        setShowDropdown(false);
    };

    const handleDropPost = async (e) => {
        e.preventDefault();

        if (!movieId) {
            alert("Bro, you need to select a valid movie from the dropdown first.");
            return;
        }

        const payload = {
            userId: currentUserId,
            movieId: parseInt(movieId),
            text: text,
            isCampaign: isCampaign,
            fundingTarget: isCampaign ? parseFloat(fundingTarget) : 0
        };

        try {
            // FIX: Removed AWS URL, starts with /api now
            const response = await fetch("/api/posts/create", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                alert("Post launched successfully!");
                setText("");
                setMovieId("");
                setMovieTitle("");
                setIsCampaign(false);
                setFundingTarget("");
            } else {
                alert("Backend rejected the post. Check console.");
            }
        } catch (error) {
            console.error("Server connection failed:", error);
        }
    };

    return (
        <div style={{ backgroundColor: '#1e1e1e', padding: '20px', borderRadius: '8px', color: '#fff', maxWidth: '600px', margin: '0 auto' }}>
            <h2>Drop a Post or Launch a Campaign</h2>

            <form onSubmit={handleDropPost} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>

                {/* AUTOCOMPLETE SEARCH WRAPPER */}
                <div style={{ position: 'relative' }}>
                    <input
                        type="text"
                        placeholder="Search for a movie..."
                        value={movieTitle}
                        onChange={(e) => {
                            setMovieTitle(e.target.value);
                            setMovieId(""); // Reset the ID instantly if they start typing something new
                        }}
                        style={{ padding: '10px', borderRadius: '4px', border: 'none', width: '100%', boxSizing: 'border-box' }}
                    />

                    {/* DROPDOWN MENU UI */}
                    {showDropdown && searchResults.length > 0 && (
                        <ul style={{
                            position: 'absolute', top: '100%', left: 0, right: 0,
                            backgroundColor: '#2a2a2a', border: '1px solid #333',
                            borderRadius: '4px', listStyle: 'none', padding: 0, margin: '5px 0 0 0',
                            maxHeight: '200px', overflowY: 'auto', zIndex: 1000
                        }}>
                            {searchResults.map((movie) => (
                                <li
                                    key={movie.movieId || movie.id}
                                    onClick={() => handleSelectMovie(movie.movieId || movie.id, movie.title)}
                                    style={{ padding: '10px', cursor: 'pointer', borderBottom: '1px solid #444' }}
                                    onMouseEnter={(e) => e.target.style.backgroundColor = '#3a3a3a'}
                                    onMouseLeave={(e) => e.target.style.backgroundColor = 'transparent'}
                                >
                                    <strong>{movie.title}</strong>
                                </li>
                            ))}
                        </ul>
                    )}
                </div>

                <textarea
                    placeholder="What are your thoughts on this movie?"
                    value={text}
                    onChange={(e) => setText(e.target.value)}
                    required
                    rows="4"
                    style={{ padding: '10px', borderRadius: '4px', border: 'none' }}
                />

                <label style={{ display: 'flex', alignItems: 'center', gap: '10px', cursor: 'pointer' }}>
                    <input
                        type="checkbox"
                        checked={isCampaign}
                        onChange={(e) => setIsCampaign(e.target.checked)}
                        style={{ width: '20px', height: '20px' }}
                    />
                    Launch as a Crowdfunding Campaign?
                </label>

                {isCampaign && (
                    <input
                        type="number"
                        placeholder="Enter Funding Target ($)"
                        value={fundingTarget}
                        onChange={(e) => setFundingTarget(e.target.value)}
                        required={isCampaign}
                        style={{ padding: '10px', borderRadius: '4px', border: '2px solid #00d8ff' }}
                    />
                )}

                <button
                    type="submit"
                    style={{ padding: '12px', backgroundColor: isCampaign ? '#00d8ff' : '#00ff88', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold', color: '#000' }}>
                    {isCampaign ? "Launch Campaign 🚀" : "Post Update"}
                </button>
            </form>
        </div>
    );
};

export default CreatePost;
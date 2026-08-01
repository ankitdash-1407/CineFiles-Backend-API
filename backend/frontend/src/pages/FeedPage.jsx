import React, { useState, useEffect, useContext } from 'react';
import CreatePost from '../components/CreatePost';
import InvestmentModal from '../components/InvestmentModal';
import { AuthContext } from '../context/AuthContext';

const FeedPage = () => {
    const [posts, setPosts] = useState([]);
    const { user, token } = useContext(AuthContext);

    // Modal & UI States
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [activeCampaign, setActiveCampaign] = useState(null);
    const [notification, setNotification] = useState(null); // The banner state

    // Helper to fire off the sleek banner
    const showNotification = (msg, type = 'success') => {
        setNotification({ msg, type });
        setTimeout(() => setNotification(null), 3000); // Kills banner after 3s
    };

    const fetchFeed = async () => {
        if (!token) return;
        try {
            const response = await fetch("http://cinefiles-api.ap-south-1.elasticbeanstalk.com/api/posts/feed", {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });
            if (!response.ok) return;
            const data = await response.json();
            setPosts(data);
        } catch (error) {
            console.error("Failed to fetch the feed:", error);
        }
    };

    const openInvestmentModal = (postId, currentRaised, fundingTarget) => {
        const remaining = fundingTarget - currentRaised;
        if (remaining <= 0) {
            showNotification("This campaign is already fully funded!", "error");
            return;
        }
        setActiveCampaign({ postId, maxAmount: remaining });
        setIsModalOpen(true);
    };

    const handleInvestSubmit = async (amount) => {
        try {
            const payload = {
                userId: user.userId,
                postId: activeCampaign.postId,
                amount: amount
            };

            const response = await fetch("http://cinefiles-api.ap-south-1.elasticbeanstalk.com/api/investments/invest", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                showNotification("Investment locked in Escrow! 💸", "success");
                setIsModalOpen(false);
                fetchFeed();
            } else {
                const err = await response.text();
                showNotification("Transaction failed: " + err, "error");
            }
        } catch (error) {
            showNotification("Server unreachable.", "error");
        }
    };

    useEffect(() => {
        fetchFeed();
    }, [token]);

    return (
        <div style={{ padding: '20px', maxWidth: '800px', margin: '0 auto', color: '#fff', position: 'relative' }}>

            {/* THE NOTIFICATION BANNER */}
            {notification && (
                <div style={{
                    padding: '15px',
                    marginBottom: '20px',
                    borderRadius: '4px',
                    backgroundColor: notification.type === 'success' ? '#00ff88' : '#ff4444',
                    color: '#000',
                    fontWeight: 'bold',
                    textAlign: 'center',
                    boxShadow: '0 4px 10px rgba(0,0,0,0.3)',
                    transition: 'all 0.3s ease-in-out'
                }}>
                    {notification.msg}
                </div>
            )}

            <InvestmentModal
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                onSubmit={handleInvestSubmit}
                maxAmount={activeCampaign?.maxAmount || 0}
            />

            {user && <CreatePost currentUserId={user.userId} />}

            <h2 style={{ borderBottom: '2px solid #333', paddingBottom: '10px', marginTop: '40px' }}>Global Feed</h2>

            {posts.length === 0 ? <p>No posts yet. Be the first to drop one!</p> : null}

            {posts.map((post) => (
                <div key={post.postId} style={{ backgroundColor: '#1e1e1e', padding: '20px', borderRadius: '8px', marginBottom: '20px', border: '1px solid #333' }}>
                    <h3 style={{ margin: '0 0 10px 0', color: '#fff' }}>
                        {post.author?.username} <span style={{ color: '#aaa', fontWeight: 'normal' }}>on</span> {post.movie?.title}
                    </h3>
                    <p style={{ color: '#ccc', marginBottom: '15px' }}>{post.text}</p>

                    {post.campaign || post.isCampaign ? (
                        <div style={{ backgroundColor: '#2a2a2a', padding: '15px', borderRadius: '8px', border: '1px solid #00d8ff' }}>
                            <h4 style={{ color: '#00d8ff', margin: '0 0 10px 0' }}>🚀 Crowdfunding Campaign</h4>

                            <div style={{ width: '100%', backgroundColor: '#333', height: '10px', borderRadius: '5px', marginBottom: '10px' }}>
                                <div style={{ width: `${Math.min((post.currentRaised / post.fundingTarget) * 100, 100)}%`, backgroundColor: '#00ff88', height: '100%', borderRadius: '5px', transition: 'width 0.5s ease-in-out' }}></div>
                            </div>

                            <p style={{ fontSize: '14px', margin: '0 0 10px 0' }}>${post.currentRaised} / ${post.fundingTarget}</p>

                            <button
                                onClick={() => openInvestmentModal(post.postId, post.currentRaised, post.fundingTarget)}
                                style={{ padding: '8px 16px', backgroundColor: '#00d8ff', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold', color: '#000' }}>
                                Invest
                            </button>
                        </div>
                    ) : (
                        <div style={{ display: 'flex', gap: '15px' }}>
                            <button style={{ background: 'none', border: 'none', color: '#aaa', cursor: 'pointer' }}>❤️ {post.likeCount} Likes</button>
                            <button style={{ background: 'none', border: 'none', color: '#aaa', cursor: 'pointer' }}>💬 Comment</button>
                        </div>
                    )}
                </div>
            ))}
        </div>
    );
};

export default FeedPage;
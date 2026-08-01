import React, { useState, useEffect, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import InvestmentModal from '../components/InvestmentModal';
import { createRazorpayOrder } from '../services/api';

const CampaignsPage = () => {
    const [bonds, setBonds] = useState([]);
    const [portfolio, setPortfolio] = useState([]);
    const { user, token } = useContext(AuthContext);

    // Modal & UI States
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [activeCampaign, setActiveCampaign] = useState(null);
    const [notification, setNotification] = useState(null);

    // The Sleek Banner
    const showNotification = (msg, type = 'success') => {
        setNotification({ msg, type });
        setTimeout(() => setNotification(null), 3000);
    };

    // Fetches the global market and filters for campaigns only
    const fetchBonds = async () => {
        if (!token) return;
        try {
            const response = await fetch("http://cinefiles-api.ap-south-1.elasticbeanstalk.com/api/posts/feed", {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (!response.ok) return;

            const data = await response.json();
            setBonds(data.filter(post => post.isCampaign || post.campaign));
        } catch (error) {
            console.error("Failed to fetch bond market", error);
        }
    };

    // Fetches YOUR specific investments
    const fetchPortfolio = async () => {
        if (!token || !user) return;
        try {
            const response = await fetch(`http://cinefiles-api.ap-south-1.elasticbeanstalk.com/api/investments/portfolio/${user.userId}`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (!response.ok) return;

            const data = await response.json();
            setPortfolio(data);
        } catch (error) {
            console.error("Failed to fetch portfolio", error);
        }
    };

    // Replaces window.prompt to trigger the custom React Modal
    const openInvestmentModal = (postId, currentRaised, fundingTarget) => {
        const remaining = fundingTarget - currentRaised;
        if (remaining <= 0) {
            showNotification("This campaign is already fully funded!", "error");
            return;
        }

        setActiveCampaign({ postId, maxAmount: remaining });
        setIsModalOpen(true);
    };

    // Handles the actual API call when you submit the Modal
    const handleInvestSubmit = async (amount) => {
        try {
            // 1. Get the Order ID from your Spring Boot backend
            const orderData = await createRazorpayOrder(amount, token);

            // 2. Configure the Razorpay Popup
            const options = {
                key: "rzp_test_TJqYejHy0zzmFR", // Your public test key
                amount: orderData.amount,
                currency: orderData.currency,
                name: "CineFiles Escrow",
                description: `Investment in ${activeCampaign.postId}`,
                order_id: orderData.id,
                theme: { color: "#00d8ff" },

                // 3. What happens when payment succeeds
                handler: async function (response) {
                    // Payment worked! NOW we tell the database.
                    const payload = {
                        userId: user.userId,
                        postId: activeCampaign.postId,
                        amount: amount
                    };

                    const dbResponse = await fetch("http://cinefiles-api.ap-south-1.elasticbeanstalk.com/api/investments/invest", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json",
                            "Authorization": `Bearer ${token}`
                        },
                        body: JSON.stringify(payload)
                    });

                    if (dbResponse.ok) {
                        showNotification("Investment locked in Escrow! 💸", "success");
                        setIsModalOpen(false);
                        fetchBonds();
                        fetchPortfolio();
                    }
                }
            };

            // 4. Fire the popup!
            const rzp = new window.Razorpay(options);
            rzp.on('payment.failed', function (response) {
                showNotification("Payment failed or cancelled.", "error");
            });
            rzp.open();

        } catch (error) {
            showNotification("Failed to connect to Escrow gateway.", "error");
        }
    };

    useEffect(() => {
        fetchBonds();
        fetchPortfolio();
    }, [token, user]);

    return (
        <div style={{ padding: '20px', maxWidth: '1000px', margin: '0 auto', color: '#fff', position: 'relative' }}>

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

            {/* THE INVESTMENT MODAL */}
            <InvestmentModal
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                onSubmit={handleInvestSubmit}
                maxAmount={activeCampaign?.maxAmount || 0}
            />

            {/* --- TOP SECTION: MY PORTFOLIO --- */}
            <div style={{ backgroundColor: '#1a1a1a', padding: '20px', borderRadius: '8px', border: '1px solid #444', marginBottom: '40px' }}>
                <h2 style={{ color: '#00ff88', marginTop: 0, borderBottom: '1px solid #333', paddingBottom: '10px' }}>💼 My Portfolio</h2>

                {portfolio.length === 0 ? (
                    <p style={{ color: '#aaa' }}>No investments yet.</p>
                ) : (
                    <>
                        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))', gap: '15px', marginBottom: '30px' }}>
                            {portfolio.map((item) => (
                                <div key={item.investmentId} style={{ backgroundColor: '#2a2a2a', padding: '15px', borderRadius: '6px', borderLeft: '4px solid #00ff88' }}>
                                    <h4 style={{ margin: '0 0 5px 0', color: '#fff' }}>{item.post?.movie?.title || "Unknown"}</h4>
                                    <p style={{ margin: 0, color: '#ccc', fontSize: '14px' }}>Invested: <strong style={{ color: '#00ff88' }}>${item.amountInvested}</strong></p>
                                </div>
                            ))}
                        </div>

                        <h3 style={{ color: '#fff', fontSize: '18px', marginBottom: '15px' }}>Transaction History</h3>
                        <table style={{ width: '100%', borderCollapse: 'collapse', color: '#ccc', fontSize: '14px' }}>
                            <thead>
                            <tr style={{ borderBottom: '1px solid #333', textAlign: 'left' }}>
                                <th style={{ padding: '10px' }}>Movie</th>
                                <th style={{ padding: '10px' }}>Amount</th>
                                <th style={{ padding: '10px' }}>Status</th>
                                <th style={{ padding: '10px' }}>Date</th>
                            </tr>
                            </thead>
                            <tbody>
                            {portfolio.map((item) => (
                                <tr key={item.investmentId} style={{ borderBottom: '1px solid #222' }}>
                                    <td style={{ padding: '10px' }}>{item.post?.movie?.title}</td>
                                    <td style={{ padding: '10px', color: '#00ff88' }}>+${item.amountInvested}</td>
                                    <td style={{ padding: '10px' }}>{item.paymentStatus}</td>
                                    <td style={{ padding: '10px', color: '#888' }}>
                                        {item.transactionDate ? new Date(item.transactionDate).toLocaleString() : 'N/A'}
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </>
                )}
            </div>

            {/* --- BOTTOM SECTION: ACTIVE MARKET --- */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '2px solid #00d8ff', paddingBottom: '10px', marginBottom: '30px' }}>
                <h2 style={{ margin: 0, color: '#00d8ff' }}>💸 Active Bond Market</h2>
                <span style={{ backgroundColor: '#1e1e1e', padding: '8px 16px', borderRadius: '4px', border: '1px solid #333' }}>
                    Live Campaigns: <strong>{bonds.length}</strong>
                </span>
            </div>

            {bonds.length === 0 ? (
                <p style={{ color: '#aaa', textAlign: 'center', marginTop: '50px' }}>No active campaigns on the market right now.</p>
            ) : null}

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '20px' }}>
                {bonds.map((bond) => (
                    <div key={bond.postId} style={{ backgroundColor: '#1e1e1e', padding: '20px', borderRadius: '8px', border: '1px solid #333', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>

                        <div>
                            <h3 style={{ margin: '0 0 5px 0', color: '#fff' }}>{bond.movie?.title}</h3>
                            <p style={{ color: '#aaa', fontSize: '14px', margin: '0 0 15px 0' }}>Launched by: {bond.author?.username}</p>
                            <p style={{ color: '#ccc', marginBottom: '20px', fontSize: '15px' }}>"{bond.text}"</p>
                        </div>

                        <div style={{ backgroundColor: '#121212', padding: '15px', borderRadius: '6px', border: '1px solid #2a2a2a' }}>
                            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '14px', marginBottom: '8px' }}>
                                <span style={{ color: '#00ff88' }}>Raised: ${bond.currentRaised}</span>
                                <span style={{ color: '#aaa' }}>Target: ${bond.fundingTarget}</span>
                            </div>

                            <div style={{ width: '100%', backgroundColor: '#333', height: '8px', borderRadius: '4px', marginBottom: '15px' }}>
                                <div style={{ width: `${Math.min((bond.currentRaised / bond.fundingTarget) * 100, 100)}%`, backgroundColor: '#00ff88', height: '100%', borderRadius: '4px' }}></div>
                            </div>

                            <button
                                onClick={() => openInvestmentModal(bond.postId, bond.currentRaised, bond.fundingTarget)}
                                style={{ width: '100%', padding: '10px', backgroundColor: '#00d8ff', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold', color: '#000' }}>
                                Invest in Bond
                            </button>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default CampaignsPage;
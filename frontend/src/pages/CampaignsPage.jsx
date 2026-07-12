import React, { useContext, useEffect, useState } from 'react';
import { AuthContext } from '../context/AuthContext';
import Layout from '../components/Layout';
import { getCampaigns, investInCampaign } from '../services/api';

function CampaignsPage() {
    const { user } = useContext(AuthContext);
    const [campaigns, setCampaigns] = useState([]);
    const [investAmount, setInvestAmount] = useState({});
    const [message, setMessage] = useState('');

    useEffect(() => {
        fetchCampaigns();
    }, []);

    const fetchCampaigns = async () => {
        try {
            const data = await getCampaigns();
            setCampaigns(data);
        } catch (err) {
            console.error(err);
        }
    };

    const handleInvest = async (campaignId) => {
        const amount = investAmount[campaignId];
        if (!amount || amount <= 0) return alert("Enter a valid amount, bro.");

        try {
            const responseMessage = await investInCampaign(user.id, campaignId, amount);
            // THE FIX: Pulls the exact string from the JSON so React doesn't white-screen
            setMessage(responseMessage.message);
            fetchCampaigns();
        } catch (err) {
            setMessage(err.error || "Transaction failed.");
        }
    };

    const handleAmountChange = (campaignId, value) => {
        setInvestAmount({ ...investAmount, [campaignId]: value });
    };

    if (!user) return (
        <div style={{ padding: '50px', color: 'white', backgroundColor: '#121212', height: '100vh' }}>
            <h2>Access Denied. Log in to access the Bond Market.</h2>
        </div>
    );

    return (
        <Layout>
            <h1 style={{ marginTop: 0, color: '#00d8ff' }}>Live Movie Bonds</h1>
            {/* The success text will now print here cleanly without crashing */}
            {message && <p style={{ color: '#00ff88', fontWeight: 'bold' }}>{message}</p>}

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '20px', marginTop: '20px' }}>
                {campaigns.map((camp) => {
                    const raised = camp.currentRaised || 0;
                    const target = camp.fundingTarget || 1;
                    const progress = Math.min((raised / target) * 100, 100).toFixed(2);

                    return (
                        <div key={camp.campaignId} style={{ backgroundColor: '#1e1e1e', padding: '20px', borderRadius: '8px', border: '1px solid #333' }}>
                            <h3 style={{ margin: '0 0 10px 0', color: '#fff' }}>Movie ID: {camp.movie?.title || camp.movie?.id || "N/A"}</h3>
                            <p style={{ color: '#aaa', margin: '0 0 5px 0', fontSize: '14px' }}>Status: <strong>{camp.status}</strong></p>

                            {/* Progress Bar UI */}
                            <div style={{ width: '100%', backgroundColor: '#333', borderRadius: '4px', height: '10px', marginBottom: '10px' }}>
                                <div style={{ width: `${progress}%`, backgroundColor: '#00ff88', height: '100%', borderRadius: '4px' }}></div>
                            </div>

                            <p style={{ color: '#fff', fontSize: '14px', marginBottom: '15px' }}>
                                ${raised} / ${target} ({progress}%)
                            </p>

                            {camp.status === 'ACTIVE' && (
                                <div style={{ display: 'flex', gap: '10px' }}>
                                    <input
                                        type="number"
                                        placeholder="$ Amount"
                                        value={investAmount[camp.campaignId] || ''}
                                        onChange={(e) => handleAmountChange(camp.campaignId, e.target.value)}
                                        style={{ flex: 1, padding: '8px', borderRadius: '4px', border: 'none', backgroundColor: '#333', color: 'white' }}
                                    />
                                    <button
                                        onClick={() => handleInvest(camp.campaignId)}
                                        style={{ padding: '8px 16px', backgroundColor: '#00d8ff', color: '#000', border: 'none', borderRadius: '4px', fontWeight: 'bold', cursor: 'pointer' }}>
                                        Invest
                                    </button>
                                </div>
                            )}
                        </div>
                    )
                })}
            </div>
        </Layout>
    );
}

export default CampaignsPage;
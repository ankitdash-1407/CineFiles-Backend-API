import React, { useState } from 'react';

const InvestmentModal = ({ isOpen, onClose, onSubmit, maxAmount }) => {
    const [amount, setAmount] = useState("");

    if (!isOpen) return null;

    const handleSubmit = (e) => {
        e.preventDefault();
        const investAmount = parseFloat(amount);

        if (investAmount > maxAmount || investAmount <= 0) {
            alert(`Invalid amount. Max is $${maxAmount}`);
            return;
        }

        onSubmit(investAmount);
        setAmount(""); // Reset for next time
    };

    return (
        <div style={{
            position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
            backgroundColor: 'rgba(0,0,0,0.8)', display: 'flex',
            justifyContent: 'center', alignItems: 'center', zIndex: 1000
        }}>
            <div style={{
                backgroundColor: '#1e1e1e', padding: '25px', borderRadius: '8px',
                border: '1px solid #00d8ff', width: '320px', color: '#fff',
                boxShadow: '0 4px 15px rgba(0, 216, 255, 0.2)'
            }}>
                <h3 style={{ marginTop: 0, color: '#00d8ff' }}>Lock In Investment</h3>
                <p style={{ color: '#aaa', fontSize: '14px', marginBottom: '15px' }}>
                    Available Escrow Allocation: <strong style={{ color: '#00ff88' }}>${maxAmount}</strong>
                </p>

                <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                    <input
                        type="number"
                        placeholder="Enter Amount ($)"
                        value={amount}
                        onChange={(e) => setAmount(e.target.value)}
                        max={maxAmount}
                        min="1"
                        step="1"
                        required
                        style={{ padding: '12px', borderRadius: '4px', border: 'none', backgroundColor: '#333', color: '#fff' }}
                    />
                    <div style={{ display: 'flex', gap: '10px', justifyContent: 'space-between' }}>
                        <button
                            type="button"
                            onClick={onClose}
                            style={{ flex: 1, padding: '10px', background: 'transparent', color: '#aaa', border: '1px solid #444', borderRadius: '4px', cursor: 'pointer' }}>
                            Cancel
                        </button>
                        <button
                            type="submit"
                            style={{ flex: 1, padding: '10px', backgroundColor: '#00d8ff', color: '#000', border: 'none', borderRadius: '4px', fontWeight: 'bold', cursor: 'pointer' }}>
                            Invest 💸
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default InvestmentModal;
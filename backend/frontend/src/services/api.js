import axios from 'axios';

// FIX: Set to empty string so Vercel can proxy it
const API_BASE_URL = '';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    }
});

export const loginUser = async (credentials) => {
    try {
        const response = await api.post('/api/auth/login', credentials);
        return response.data;
    } catch (error) {
        console.error("API Error:", error);
        throw error;
    }
};

export const registerUser = async (userData) => {
    try {
        const response = await api.post('/api/auth/register', userData);
        return response.data;
    } catch (error) {
        console.error("API Error:", error);
        throw error;
    }
};

// --- MOVIE API CALLS ---

export const searchMovie = async (title, token) => {
    try {
        const response = await fetch(`${API_BASE_URL}/api/movies/search?title=${title}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) throw new Error("Server rejected request");
        return await response.json();
    } catch (error) {
        console.error(error);
        throw error;
    }
};

// Add to Watchlist
export const addToWatchlist = async (username, title, token) => {
    try {
        const response = await fetch(`${API_BASE_URL}/api/movies/watchlist/add?username=${username}&title=${title}`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        if (!response.ok) throw new Error("Failed to add to watchlist");
        return await response.json();
    } catch (error) {
        console.error(error);
        throw error;
    }
};

// Get Watchlist
export const getWatchlist = async (username, token) => {
    try {
        const response = await fetch(`${API_BASE_URL}/api/movies/watchlist?username=${username}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        if (!response.ok) throw new Error("Failed to fetch watchlist");
        return await response.json();
    } catch (error) {
        console.error(error);
        throw error;
    }
};

// --- INVESTMENT API CALLS ---

// Fetch all active crowdfunding campaigns
export const getCampaigns = async (token) => {
    try {
        const response = await fetch(`${API_BASE_URL}/api/investments/campaigns`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        if (!response.ok) throw new Error("Failed to fetch campaigns");
        return await response.json();
    } catch (error) {
        console.error(error);
        throw error;
    }
};

// Send the money to the Escrow
export const investInCampaign = async (payload, token) => {
    try {
        const response = await fetch(`${API_BASE_URL}/api/investments/invest`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const err = await response.text();
            throw new Error(err);
        }
        return response;
    } catch (error) {
        console.error(error);
        throw error;
    }
};

export const createRazorpayOrder = async (amount, token) => {
    try {
        const response = await fetch(`${API_BASE_URL}/api/investments/create-order`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ amount })
        });

        if (!response.ok) throw new Error("Failed to create order");
        const data = await response.text();
        return JSON.parse(data);
    } catch (error) {
        console.error(error);
        throw error;
    }
};

export default api;
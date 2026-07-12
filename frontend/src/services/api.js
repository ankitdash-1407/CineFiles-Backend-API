import axios from 'axios';

// Your Spring Boot server runs on 8080
const API_BASE_URL = 'http://localhost:8080';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    }
});

export const loginUser = async (credentials) => {
    try {
        // This sends a POST request to http://localhost:8080/users/login
        const response = await api.post('/api/users/login', credentials);
        return response.data;
    } catch (error) {
        console.error("API Error:", error);
        throw error;
    }
};

export const registerUser = async (userData) => {
    try {
        const response = await api.post('/api/users/register', userData);
        return response.data;
    } catch (error) {
        console.error("API Error:", error);
        throw error;
    }
};

// --- MOVIE API CALLS ---

export const searchMovie = async (title) => {
    try {
        const response = await api.get(`/api/movies/search?title=${title}`);
        return response.data;
    } catch (error) {
        throw error.response?.data || { error: "Failed to connect to server" };
    }
};

export const addToWatchlist = async (username, title) => {
    try {
        // Controller expects @RequestParam, so we put them right in the URL
        const response = await api.post(`/api/movies/watchlist/add?username=${username}&title=${title}`);
        return response.data;
    } catch (error) {
        throw error.response?.data || { error: "Failed to add to watchlist" };
    }
};

export const getWatchlist = async (username) => {
    try {
        const response = await api.get(`/api/movies/watchlist?username=${username}`);
        return response.data;
    } catch (error) {
        throw error.response?.data || { error: "Failed to fetch watchlist" };
    }
};

// Fetch all active crowdfunding campaigns
export const getCampaigns = async () => {
    try {
        const response = await api.get('/api/investments/campaigns');
        return response.data;
    } catch (error) {
        throw error.response?.data || { error: "Failed to fetch campaigns" };
    }
};

// Send the money to the Escrow
export const investInCampaign = async (userId, campaignId, amount) => {
    try {
        const response = await api.post('/api/investments/invest', null, {
            params: { userId, campaignId, amount }
        });
        return response.data;
    } catch (error) {
        throw error.response?.data || { error: "Investment failed" };
    }
};

export default api;
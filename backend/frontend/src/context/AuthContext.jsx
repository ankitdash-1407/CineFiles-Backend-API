import React, { createContext, useState, useEffect } from 'react';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    // 1. NEW: Track the JWT token
    const [token, setToken] = useState(localStorage.getItem('token') || null);

    useEffect(() => {
        // 2. UPDATED: Only log them in if they have BOTH the user data AND a valid token
        const storedUser = localStorage.getItem('user');
        if (storedUser && token) {
            setUser(JSON.parse(storedUser));
        }
    }, [token]);

    // 3. UPDATED: Catch the token from the backend and save it to local storage
    const login = (userData, jwtToken) => {
        setUser(userData);
        setToken(jwtToken);
        localStorage.setItem('user', JSON.stringify(userData));
        localStorage.setItem('token', jwtToken);
    };

    // 4. UPDATED: Shred the token on logout and kick them to the login screen
    const logout = () => {
        setUser(null);
        setToken(null);
        localStorage.removeItem('user');
        localStorage.removeItem('token');
        window.location.href = '/login';
    };

    return (
        // 5. NEW: Expose the token to the rest of the app so your fetch() calls can use it
        <AuthContext.Provider value={{ user, token, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};
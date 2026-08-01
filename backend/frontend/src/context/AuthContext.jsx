import React, { createContext, useState, useEffect } from 'react';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [token, setToken] = useState(null);

    useEffect(() => {
        // Load data on refresh
        const savedUser = localStorage.getItem('user');
        const savedToken = localStorage.getItem('token');

        // FIX: Make absolutely sure the token isn't the literal string 'undefined'
        if (savedUser && savedToken && savedToken !== 'undefined') {
            setUser(JSON.parse(savedUser));
            setToken(savedToken);
        }
    }, []);

    const login = (userData, jwtToken) => {
        if (!jwtToken) {
            console.error("CRITICAL: Token is missing! Check if backend sends 'token' or 'jwt'");
            return;
        }
        setUser(userData);
        setToken(jwtToken);
        localStorage.setItem('user', JSON.stringify(userData));
        localStorage.setItem('token', jwtToken);
    };

    const logout = () => {
        setUser(null);
        setToken(null);
        localStorage.removeItem('user');
        localStorage.removeItem('token');
    };

    return (
        <AuthContext.Provider value={{ user, token, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};
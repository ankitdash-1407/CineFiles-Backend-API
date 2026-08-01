import React, { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { loginUser } from '../services/api';

function LoginPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');
    const { login } = useContext(AuthContext);
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setMessage('Authenticating...');

        try {
            // FIX: Using your configured Axios instance from api.js
            const data = await loginUser({ username, password });

            login(
                { userId: data.userId, username: data.username, role: data.role },
                data.token
            );

            navigate('/dashboard');

        } catch (err) {
            console.error("Login crash:", err);
            setMessage('Login failed. Check credentials or server connection.');
        }
    };

    return (
        <div style={{ padding: '50px' }}>
            <h1>Login to CineFiles</h1>
            <form onSubmit={handleLogin}>
                <div style={{ marginBottom: '10px' }}>
                    <input
                        type="text"
                        placeholder="Username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </div>
                <div style={{ marginBottom: '10px' }}>
                    <input
                        type="password"
                        placeholder="Password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <button type="submit">Sign In</button>
            </form>
            {message && <p style={{ marginTop: '20px', fontWeight: 'bold', color: 'red' }}>{message}</p>}
            <p style={{ marginTop: '20px' }}>
                Don't have an account? <a href="/register">Sign up here</a>
            </p>
        </div>
    );
}

export default LoginPage;
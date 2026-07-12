import React, { useState } from 'react';
import { loginUser } from '../services/api';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { useContext } from 'react'; // add to your React import

function LoginPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState(''); // To show success/error on screen
    const { login } = useContext(AuthContext);
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setMessage('Connecting to server...');

        try {
            const data = await loginUser({ username, password });
            login(data); // Save user to Global State
            navigate('/dashboard'); // Kick them over to the Dashboard
        } catch (err) {
            setMessage('Login failed. Check your credentials or server status.');
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
            {message && <p style={{ marginTop: '20px', fontWeight: 'bold' }}>{message}</p>}
            <p style={{ marginTop: '20px' }}>
                Don't have an account? <a href="/register">Sign up here</a>
            </p>
        </div>
    );
}

export default LoginPage;
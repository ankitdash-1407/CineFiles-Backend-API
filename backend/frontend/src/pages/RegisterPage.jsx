import React, { useState } from 'react';
import { registerUser } from '../services/api';

function RegisterPage() {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');

    const handleRegister = async (e) => {
        e.preventDefault();
        setMessage('Creating account...');

        try {
            const data = await registerUser({ username, email, password });
            setMessage(`Success! Account created for ${data.username}. You can now log in.`);
        } catch (err) {
            setMessage(err.response?.data?.error || 'Registration failed.');
        }
    };

    return (
        <div style={{ padding: '50px' }}>
            <h1>Create an Account</h1>
            <form onSubmit={handleRegister}>
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
                        type="email"
                        placeholder="Email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
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
                <button type="submit">Sign Up</button>
            </form>
            {message && <p style={{ marginTop: '20px', fontWeight: 'bold' }}>{message}</p>}

            <p style={{ marginTop: '20px' }}>
                Already have an account? <a href="/login">Log in here</a>
            </p>
        </div>
    );
}

export default RegisterPage;
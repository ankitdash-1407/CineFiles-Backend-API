import React, { useState } from 'react';
import { registerUser } from '../services/api';

function RegisterPage() {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');
    const [isError, setIsError] = useState(false);

    const handleRegister = async (e) => {
        e.preventDefault();
        setMessage('Creating account...');
        setIsError(false);

        try {
            const data = await registerUser({ username, email, password });
            setMessage(`Success! Account created for ${data.username}. You can now log in.`);
            setIsError(false);
            // Optional: Clear form on success
            setUsername('');
            setEmail('');
            setPassword('');
        } catch (err) {
            setMessage(err.response?.data?.error || 'Registration failed.');
            setIsError(true);
        }
    };

    return (
        <div style={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            height: '100vh',
            backgroundColor: '#121212',
            color: '#fff',
            fontFamily: 'sans-serif'
        }}>
            <div style={{
                backgroundColor: '#1e1e1e',
                padding: '40px',
                borderRadius: '8px',
                border: '1px solid #333',
                width: '100%',
                maxWidth: '400px',
                boxShadow: '0 4px 15px rgba(0,0,0,0.5)'
            }}>
                <h1 style={{ textAlign: 'center', color: '#00d8ff', marginTop: 0, marginBottom: '30px' }}>
                    Create Account
                </h1>

                <form onSubmit={handleRegister} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                    <input
                        type="text"
                        placeholder="Username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                        style={{ padding: '12px', borderRadius: '4px', border: '1px solid #444', backgroundColor: '#222', color: '#fff', fontSize: '16px' }}
                    />
                    <input
                        type="email"
                        placeholder="Email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                        style={{ padding: '12px', borderRadius: '4px', border: '1px solid #444', backgroundColor: '#222', color: '#fff', fontSize: '16px' }}
                    />
                    <input
                        type="password"
                        placeholder="Password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                        style={{ padding: '12px', borderRadius: '4px', border: '1px solid #444', backgroundColor: '#222', color: '#fff', fontSize: '16px' }}
                    />
                    <button
                        type="submit"
                        style={{
                            padding: '12px',
                            backgroundColor: '#00d8ff',
                            color: '#000',
                            border: 'none',
                            borderRadius: '4px',
                            fontSize: '16px',
                            fontWeight: 'bold',
                            cursor: 'pointer',
                            marginTop: '10px',
                            transition: 'background-color 0.2s'
                        }}
                    >
                        Sign Up
                    </button>
                </form>

                {message && (
                    <p style={{
                        marginTop: '20px',
                        padding: '10px',
                        borderRadius: '4px',
                        backgroundColor: isError ? '#331515' : '#153320',
                        color: isError ? '#ff4444' : '#00ff88',
                        textAlign: 'center',
                        border: `1px solid ${isError ? '#ff4444' : '#00ff88'}`,
                        fontWeight: 'bold'
                    }}>
                        {message}
                    </p>
                )}

                <p style={{ textAlign: 'center', marginTop: '25px', color: '#aaa', fontSize: '14px' }}>
                    Already have an account?{' '}
                    <a href="/login" style={{ color: '#00d8ff', textDecoration: 'none', fontWeight: 'bold' }}>
                        Log in here
                    </a>
                </p>
            </div>
        </div>
    );
}

export default RegisterPage;
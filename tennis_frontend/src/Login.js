import React, { useState } from 'react';
import { TextField, Button, Typography, Container, CssBaseline, Box, Alert } from '@mui/material';
import { useNavigate } from 'react-router-dom';

function LoginPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (event) => {
        event.preventDefault();
        setError('');

        try {
            const response = await fetch('/api/user/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });
            if (!response.ok) {
                throw new Error('Failed to login');
            }
            const user = await response.json();
            localStorage.setItem('user', JSON.stringify(user));

            switch (user.userType) {
                case 'administrator':
                    navigate('/admin-schedule');
                    break;
                case 'referee':
                    navigate('/referee-schedule');
                    break;
                case 'player':
                    navigate('/player-schedule');
                    break;
                default:
                    navigate('/login');
            }
        } catch (err) {
            setError(err.message || 'Unexpected Error');
        }
    };

    return (
        <Container component="main" maxWidth="xs">
            <CssBaseline />
            <Box
                sx={{
                    marginTop: 8,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                }}
            >
                <Typography component="h1" variant="h5">
                    Sign in
                </Typography>
                {error && <Alert severity="error">{error}</Alert>}
                <Box component="form" onSubmit={handleLogin} noValidate sx={{ mt: 1 }}>
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        label="Username"
                        name="username"
                        autoComplete="username"
                        autoFocus
                        value={username}
                        onChange={e => setUsername(e.target.value)}
                    />
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        name="password"
                        label="Password"
                        type="password"
                        autoComplete="current-password"
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                    />
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        sx={{ mt: 3, mb: 2 }}
                    >
                        Sign In
                    </Button>
                    <Button
                        fullWidth
                        variant="text"
                        onClick={() => navigate('/signup')}
                    >
                        Don't have an account? Sign Up
                    </Button>
                </Box>
            </Box>
        </Container>
    );
}

export default LoginPage;

import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { TextField, Button, Typography, Container, CssBaseline, Box, Alert } from '@mui/material';
import axios from 'axios';

const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (event) => {
        event.preventDefault();
        setError('');

        try {
            const response = await axios.post('http://localhost:8081/api/user/login', {
                username,
                password
            });

            const user = response.data;
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
                    throw new Error('User type is not valid');
            }
        } catch (err) {
            const errorMessage = err.response?.data || 'Login failed. Please try again.';
            setError(errorMessage);
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
                <Typography component="h1" variant="h5" marginBottom="10px">Login</Typography>
                {error && <Alert severity="error" sx={{ width: '100%', mb: 2 }}>{error}</Alert>}
                <Box component="form" onSubmit={handleLogin} sx={{ mt: 1, width: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        label="Username"
                        name="username"
                        autoComplete="username"
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
                        variant="contained"
                        sx={{ mt: 3, mb: 2, width: '75%' }}
                    >
                        Login
                    </Button>
                    <Button
                        variant="text"
                        sx={{ mt: 1, width: '75%' }}
                        onClick={() => navigate('/signup')}
                    >
                        Don't have an account? Sign Up
                    </Button>
                </Box>
            </Box>
        </Container>
    );
};

export default Login;

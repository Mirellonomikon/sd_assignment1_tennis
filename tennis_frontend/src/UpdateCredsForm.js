import React, { useState, useEffect } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    TextField,
    Button,
    DialogActions,
    Alert
} from '@mui/material';
import axios from 'axios';

const UpdateCredsForm = ({ open, handleClose, userId }) => {
    const [userData, setUserData] = useState({ username: '', name: '', email: '', newPassword: '', oldPassword: '' });
    const [error, setError] = useState('');
    const token = localStorage.getItem('token');

    useEffect(() => {
        if (open) {
            const fetchUserDetails = async () => {
                try {
                    userId = 23;
                    const response = await axios.get(`http://localhost:8081/api/user/id?userId=${userId}`, {
                        headers: { Authorization: `Bearer ${token}` }
                    });
                    const user = response.data;
                    setUserData(prev => ({ ...prev, username: user.username, name: user.name, email: user.email }));
                    setError('');
                } catch (err) {
                    setError(err.response?.data || 'Failed to fetch user details.');
                }
            };

            fetchUserDetails();
        }
    }, [open, userId]);

    const handleInputChange = (e) => {
        setUserData({ ...userData, [e.target.name]: e.target.value });
    };

    const handleUpdate = async () => {
        try {
            const userUpdateCredentialsDTO = {
                username: userData.username,
                name: userData.name,
                email: userData.email,
                newPassword: userData.newPassword,
                oldPassword: userData.oldPassword
            };

            await axios.put(`http://localhost:8081/api/user/update?id=${userId}`, userUpdateCredentialsDTO, {
                headers: { Authorization: `Bearer ${token}` }
            });
            handleClose(true);
        } catch (err) {
            setError(err.response?.data || 'Failed to update user.');
        }
    };

    return (
        <Dialog open={open} onClose={() => handleClose(false)}
            sx={{ '& .MuiPaper-root': { backgroundColor: '#f1f8e9' } }}>
            <DialogTitle>Update Your Credentials</DialogTitle>
            <DialogContent>
                {error && <Alert severity="error" style={{ backgroundColor: '#FFF6EA', marginBottom: '5px' }}>{error}</Alert>}
                <TextField
                    autoFocus
                    margin="dense"
                    name="username"
                    label="Username"
                    type="text"
                    fullWidth
                    variant="outlined"
                    value={userData.username}
                    onChange={handleInputChange}
                />
                <TextField
                    margin="dense"
                    name="name"
                    label="Name"
                    type="text"
                    fullWidth
                    variant="outlined"
                    value={userData.name}
                    onChange={handleInputChange}
                />
                <TextField
                    autoFocus
                    margin="dense"
                    name="email"
                    label="Email"
                    type="text"
                    fullWidth
                    variant="outlined"
                    value={userData.email}
                    onChange={handleInputChange}
                />
                <TextField
                    margin="dense"
                    name="oldPassword"
                    label="Old Password"
                    type="password"
                    fullWidth
                    variant="outlined"
                    onChange={handleInputChange}
                />
                <TextField
                    margin="dense"
                    name="newPassword"
                    label="New Password"
                    type="password"
                    fullWidth
                    variant="outlined"
                    onChange={handleInputChange}
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={() => handleClose(false)} color="secondary">
                    Cancel
                </Button>
                <Button onClick={handleUpdate} color="primary">
                    Update
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default UpdateCredsForm;

import React from 'react';
import { Box, Typography, IconButton, List, ListItem, ListItemText } from '@mui/material';
import { Check, Close } from '@mui/icons-material';
import axios from 'axios';

const PendingRequests = ({ pendingUsers, fetchPendingUsers, fetchUsers }) => {
    const token = localStorage.getItem('token');

    const handleAccept = async (userId) => {
        try {
            await axios.put(`http://localhost:8081/api/user/accept-tournament?id=${userId}`, {}, {
                headers: { Authorization: `Bearer ${token}` }
            });
            fetchPendingUsers();
            fetchUsers();
        } catch (err) {
            console.error("Failed to accept user request:", err.response?.data || err.message);
        }
    };

    const handleReject = async (userId) => {
        try {
            await axios.put(`http://localhost:8081/api/user/reject-tournament?id=${userId}`, {}, {
                headers: { Authorization: `Bearer ${token}` }
            });
            fetchPendingUsers();
            fetchUsers();
        } catch (err) {
            console.error("Failed to reject user request:", err.response?.data || err.message);
        }
    };

    return (
        <Box sx={{ padding: 2, width: 300, backgroundColor: '#f1f8e9' }}>
            <Typography variant="h6">Pending Requests</Typography>
            <List>
                {pendingUsers.map((user) => (
                    <ListItem key={user.id} sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <ListItemText primary={user.username} />
                        <Box>
                            <IconButton onClick={() => handleAccept(user.id)} sx={{ color: 'green' }}>
                                <Check />
                            </IconButton>
                            <IconButton onClick={() => handleReject(user.id)} sx={{ color: 'purple' }}>
                                <Close />
                            </IconButton>
                        </Box>
                    </ListItem>
                ))}
            </List>
        </Box>
    );
};

export default PendingRequests;

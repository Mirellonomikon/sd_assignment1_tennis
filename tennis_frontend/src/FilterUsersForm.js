import React, { useState } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    TextField,
    Button,
    DialogActions,
    Select,
    MenuItem,
    FormControl,
    InputLabel,
    Alert,
} from '@mui/material';
import axios from 'axios';

const FilterUsersForm = ({ open, handleClose, setPlayers }) => {
    const [name, setName] = useState('');
    const [username, setUsername] = useState('');
    const [isCompeting, setIsCompeting] = useState('');
    const [error, setError] = useState(null);
    const token = localStorage.getItem('token');

    const resetFilters = () => {
        setName('');
        setUsername('');
        setIsCompeting('');
        setError(null);
    };

    const handleDialogClose = () => {
        resetFilters();
        handleClose();
    };

    const handleFilter = async () => {
        const params = {
            name: name || undefined,
            username: username || undefined,
            isCompeting: isCompeting === '' ? undefined : isCompeting,
        };

        try {
            const response = await axios.get('http://localhost:8081/api/user/filter/players', {
                headers: { Authorization: `Bearer ${token}` },
                params,
            });
            setPlayers(response.data);
            handleDialogClose();
        } catch (err) {
            setError(`Failed to filter players: ${err.response?.data || err.message}`);
        }
    };

    return (
        <Dialog open={open} onClose={handleDialogClose} sx={{ '& .MuiPaper-root': { backgroundColor: '#f1f8e9' } }}>
            <DialogTitle>Filter Players</DialogTitle>
            <DialogContent>
                {error && <Alert severity="error" style={{ backgroundColor: '#FFF6EA', marginBottom: '5px' }}>{error}</Alert>}
                <TextField
                    margin="dense"
                    label="Username"
                    type="text"
                    fullWidth
                    variant="outlined"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
                <TextField
                    margin="dense"
                    label="Name"
                    type="text"
                    fullWidth
                    variant="outlined"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                />
                <FormControl fullWidth margin="dense">
                    <InputLabel>Is Competing</InputLabel>
                    <Select
                        value={isCompeting}
                        label="Is Competing"
                        onChange={(e) => setIsCompeting(e.target.value)}
                    >
                        <MenuItem value="">
                            <em>None</em>
                        </MenuItem>
                        <MenuItem value={true}>Yes</MenuItem>
                        <MenuItem value={false}>No</MenuItem>
                    </Select>
                </FormControl>
            </DialogContent>
            <DialogActions>
                <Button onClick={handleDialogClose} color="secondary">
                    Cancel
                </Button>
                <Button onClick={handleFilter} color="primary">
                    Filter
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default FilterUsersForm;

import React, { useState, useEffect } from 'react';
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
    Checkbox,
    FormControlLabel
} from '@mui/material';
import axios from 'axios';

const AddUserDialog = ({ open, handleClose }) => {
    const [username, setUsername] = useState('');
    const [name, setName] = useState('');
    const [password, setPassword] = useState('');
    const [email, setEmail] = useState('');
    const [role, setRole] = useState('');
    const [roles] = useState(['referee', 'player', 'administrator']);
    const [error, setError] = useState('');
    const [isRegisteredInTournament, setIsRegisteredInTournament] = useState(false);

    const resetForm = () => {
        setUsername('');
        setName('');
        setPassword('');
        setEmail('');
        setRole('');
        setIsRegisteredInTournament(false);
        setError('');
    };

    const handleSubmit = async () => {
        try {
            const userDTO = {
                username,
                name,
                email,
                password,
                userType: role,
                isRegisteredInTournament
            };

            await axios.post('http://localhost:8081/api/user/add', userDTO);
            handleClose(true);
            resetForm();
        } catch (err) {
            setError(err.response?.data || 'Failed to add user.');
        }
    };

    const handleDialogClose = (submitSuccessful) => {
        if (!submitSuccessful) {
            resetForm();
        }
        handleClose(submitSuccessful);
    };

    useEffect(() => {
        if (role !== 'player') {
            setIsRegisteredInTournament(false);
        }
    }, [role]);

    return (
        <Dialog open={open} onClose={() => handleDialogClose(false)} sx={{ '& .MuiPaper-root': { backgroundColor: '#f1f8e9' } }}>
            <DialogTitle>Add User</DialogTitle>
            <DialogContent>
                {error && <Alert severity="error" style={{ backgroundColor: '#FFF6EA', marginBottom: '5px' }}>{error}</Alert>}
                <TextField
                    autoFocus
                    margin="dense"
                    label="Username"
                    required
                    type="text"
                    fullWidth
                    variant="outlined"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
                <TextField
                    margin="dense"
                    label="Name"
                    required
                    type="text"
                    fullWidth
                    variant="outlined"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                />
                <TextField
                    margin="dense"
                    label="Password"
                    required
                    type="password"
                    fullWidth
                    variant="outlined"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <TextField
                    margin="dense"
                    label="Email"
                    required
                    type="text"
                    fullWidth
                    variant="outlined"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
                <FormControl fullWidth margin="dense">
                    <InputLabel>Role</InputLabel>
                    <Select
                        value={role}
                        label="Role"
                        required
                        onChange={(e) => setRole(e.target.value)}
                    >
                        <MenuItem value="">
                            <em>None</em>
                        </MenuItem>
                        {roles.map((r) => (
                            <MenuItem key={r} value={r}>
                                {r}
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>
                <FormControlLabel
                    control={
                        <Checkbox
                            checked={isRegisteredInTournament}
                            onChange={(e) => setIsRegisteredInTournament(e.target.checked)}
                            name="isRegisteredInTournament"
                            disabled={role !== 'player'}
                        />
                    }
                    label="Register in Tournament"
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={() => handleDialogClose(false)} color="secondary">
                    Cancel
                </Button>
                <Button onClick={handleSubmit} color="primary">
                    Submit
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default AddUserDialog;

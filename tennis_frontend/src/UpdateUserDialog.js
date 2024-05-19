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

const UpdateUserDialog = ({ open, handleClose, userId }) => {
    const [defaultUser, setDefaultUser] = useState(null);
    const [username, setUsername] = useState('');
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [role, setRole] = useState('');
    const [roles] = useState(['referee', 'player', 'administrator']);
    const [error, setError] = useState('');
    const [isRegisteredInTournament, setIsRegisteredInTournament] = useState(false);
    const [tournamentStatus, setTournamentStatus] = useState('NONE');
    const [tournamentStatuses] = useState(['NONE', 'ACCEPTED', 'REJECTED']);
    const [originalRole, setOriginalRole] = useState('');
    const [originalTournamentStatus, setOriginalTournamentStatus] = useState(false);

    useEffect(() => {
        if (open) {
            const fetchUserDetails = async () => {
                try {
                    const response = await axios.get(`http://localhost:8081/api/user/${userId}`);
                    const user = response.data;
                    setDefaultUser(user);

                    setUsername(user.username);
                    setName(user.name);
                    setPassword(user.password);
                    setEmail(user.email);
                    setRole(user.userType);
                    setIsRegisteredInTournament(user.isRegisteredInTournament);
                    setTournamentStatus(user.tournamentRegistrationStatus);
                    setOriginalRole(user.userType);
                    setOriginalTournamentStatus(user.isRegisteredInTournament);
                } catch (err) {
                    setError(err.response?.data || 'Failed to fetch user details.');
                }
            };

            fetchUserDetails();
        } else {
            setError('');
        }
    }, [open, userId]);

    useEffect(() => {
        if (role !== 'player') {
            setIsRegisteredInTournament(false);
            setTournamentStatus('NONE');
        }
    }, [role]);

    useEffect(() => {
        if (tournamentStatus === 'ACCEPTED') {
            setIsRegisteredInTournament(true);
        } else {
            setIsRegisteredInTournament(false);
        }
    }, [tournamentStatus]);

    useEffect(() => {
        if (isRegisteredInTournament) {
            setTournamentStatus('ACCEPTED');
        } else {
            setTournamentStatus('NONE');
        }
    }, [isRegisteredInTournament]);

    const handleUpdate = async () => {
        try {
            const userDTO = {
                username,
                name,
                email,
                password,
                userType: role,
                isRegisteredInTournament,
                tournamentRegistrationStatus: tournamentStatus
            };

            if ((originalRole === 'player' && role !== 'player') || (originalTournamentStatus && !isRegisteredInTournament)) {
                const matchesResponse = await axios.get('http://localhost:8081/api/match/all');
                const matches = matchesResponse.data;
                const userMatches = matches.filter(
                    (match) => match.player1?.id === userId || match.player2?.id === userId
                );

                for (const match of userMatches) {
                    await axios.put(`http://localhost:8081/api/match/${match.id}/remove/${userId}`);
                }
            }

            await axios.put(`http://localhost:8081/api/user/${userId}`, userDTO);
            handleClose(true);
        } catch (err) {
            setError(err.response?.data || 'Failed to update user.');
        }
    };

    const handleReset = () => {
        if (defaultUser) {
            setUsername(defaultUser.username);
            setName(defaultUser.name);
            setPassword(defaultUser.password);
            setRole(defaultUser.userType);
            setEmail(defaultUser.email);
            setIsRegisteredInTournament(defaultUser.isRegisteredInTournament);
            setTournamentStatus(defaultUser.tournamentRegistrationStatus);
        }
    };

    return (
        <Dialog
            open={open}
            onClose={() => { handleClose(false); handleReset(); }}
            sx={{ '& .MuiPaper-root': { backgroundColor: '#f1f8e9' } }}
        >
            <DialogTitle>Update User</DialogTitle>
            <DialogContent>
                {error && (
                    <Alert severity="error" style={{ backgroundColor: '#FFF6EA', marginBottom: '5px' }}>{error}</Alert>
                )}
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
                    label="Email"
                    required
                    type="text"
                    fullWidth
                    variant="outlined"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
                <TextField
                    margin="dense"
                    label="Password"
                    type="password"
                    required
                    fullWidth
                    variant="outlined"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
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
                {role === 'player' && (
                    <FormControl fullWidth margin="dense">
                        <InputLabel>Tournament Status</InputLabel>
                        <Select
                            value={tournamentStatus}
                            label="Tournament Status"
                            onChange={(e) => setTournamentStatus(e.target.value)}
                        >
                            {tournamentStatuses.map((status) => (
                                <MenuItem key={status} value={status}>
                                    {status}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                )}
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

export default UpdateUserDialog;

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
    Alert
} from '@mui/material';
import axios from 'axios';

const AddMatchDialog = ({ open, handleClose }) => {
    const [name, setName] = useState('');
    const [matchDate, setMatchDate] = useState('');
    const [matchTime, setMatchTime] = useState('');
    const [location, setLocation] = useState('');
    const [referee, setReferee] = useState('');
    const [player1, setPlayer1] = useState('');
    const [player1Score, setPlayer1Score] = useState('');
    const [player2, setPlayer2] = useState('');
    const [player2Score, setPlayer2Score] = useState('');
    const [referees, setReferees] = useState([]);
    const [players, setPlayers] = useState([]);
    const [error, setError] = useState('');
    const token = localStorage.getItem('token');

    useEffect(() => {
        const fetchUsers = async (role) => {
            try {
                const response = await axios.get(`http://localhost:8081/api/user/role/${role}`, {
                    headers: { Authorization: `Bearer ${token}` }
                });
                if (role === 'referee') {
                    setReferees(response.data);
                } else {
                    setPlayers(response.data);
                }
            } catch (err) {
                setError(err.response?.data || 'Failed to fetch users.');
            }
        };

        const fetchPlayers = async () => {
            try {
                const response = await axios.get('http://localhost:8081/api/user/role/tournament', {
                    headers: { Authorization: `Bearer ${token}` }
                });

                setPlayers(response.data);
            }
            catch (err) {
                setError(err.response?.data || 'Failed to fetch players.');
            }
        };

        fetchUsers('referee');
        fetchPlayers();
    }, []);

    const resetForm = () => {
        setName('');
        setMatchDate('');
        setMatchTime('');
        setLocation('');
        setReferee('');
        setPlayer1('');
        setPlayer1Score('');
        setPlayer2('');
        setPlayer2Score('');
        setError('');
    };

    const handleSubmit = async () => {
        try {
            const matchDTO = {
                name,
                matchDate,
                matchTime,
                location,
                referee,
                player1,
                player1Score,
                player2,
                player2Score
            };

            await axios.post('http://localhost:8081/api/match/create', matchDTO, {
                headers: { Authorization: `Bearer ${token}` }
            });
            handleClose(true);
            resetForm();
        } catch (err) {
            setError(err.response?.data || 'Failed to create match.');
        }
    };

    const handleDialogClose = (submitSuccessful) => {
        if (!submitSuccessful) {
            resetForm();
        }
        handleClose(submitSuccessful);
    };

    return (
        <Dialog open={open} onClose={() => handleDialogClose(false)} sx={{ '& .MuiPaper-root': { backgroundColor: '#f1f8e9' } }}>
            <DialogTitle>Add Match</DialogTitle>
            <DialogContent>
                {error && <Alert severity="error" style={{ backgroundColor: '#FFF6EA', marginBottom: "5px" }}>{error}</Alert>}
                <TextField
                    autoFocus
                    margin="dense"
                    label="Match Name"
                    required
                    type="text"
                    fullWidth
                    variant="outlined"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                />
                <TextField
                    margin="dense"
                    label="Match Date"
                    type="date"
                    required
                    fullWidth
                    variant="outlined"
                    value={matchDate}
                    onChange={(e) => setMatchDate(e.target.value)}
                    InputLabelProps={{
                        shrink: true,
                    }}
                />
                <TextField
                    margin="dense"
                    label="Match Time"
                    type="time"
                    required
                    fullWidth
                    variant="outlined"
                    value={matchTime}
                    onChange={(e) => setMatchTime(e.target.value)}
                    InputLabelProps={{
                        shrink: true,
                    }}
                />
                <TextField
                    margin="dense"
                    label="Location"
                    type="text"
                    required
                    fullWidth
                    variant="outlined"
                    value={location}
                    onChange={(e) => setLocation(e.target.value)}
                />
                <FormControl fullWidth margin="dense">
                    <InputLabel>Referee</InputLabel>
                    <Select
                        value={referee}
                        label="Referee"
                        onChange={(e) => setReferee(e.target.value)}
                    >
                        <MenuItem value="">
                            <em>None</em>
                        </MenuItem>
                        {referees.map((user) => (
                            <MenuItem key={user.id} value={user.id}>
                                {user.name}
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>
                <FormControl fullWidth margin="dense">
                    <InputLabel>Player 1</InputLabel>
                    <Select
                        value={player1}
                        label="Player 1"
                        onChange={(e) => setPlayer1(e.target.value)}
                    >
                        <MenuItem value="">
                            <em>None</em>
                        </MenuItem>
                        {players.map((user) => (
                            <MenuItem key={user.id} value={user.id}>
                                {user.name}
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>
                <TextField
                    margin="dense"
                    label="Player 1 Score"
                    type="number"
                    fullWidth
                    variant="outlined"
                    value={player1Score}
                    onChange={(e) => setPlayer1Score(e.target.value)}
                />
                <FormControl fullWidth margin="dense">
                    <InputLabel>Player 2</InputLabel>
                    <Select
                        value={player2}
                        label="Player 2"
                        onChange={(e) => setPlayer2(e.target.value)}
                    >
                        <MenuItem value="">
                            <em>None</em>
                        </MenuItem>
                        {players.map((user) => (
                            <MenuItem key={user.id} value={user.id}>
                                {user.name}
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>
                <TextField
                    margin="dense"
                    label="Player 2 Score"
                    type="number"
                    fullWidth
                    variant="outlined"
                    value={player2Score}
                    onChange={(e) => setPlayer2Score(e.target.value)}
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

export default AddMatchDialog;

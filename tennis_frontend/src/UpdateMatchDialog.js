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
} from '@mui/material';
import axios from 'axios';


const UpdateMatchDialog = ({ open, handleClose, matchId }) => {
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

    useEffect(() => {
        const fetchMatchDetails = async () => {
            try {
                const response = await axios.get(`http://localhost:8081/api/match/${matchId}`);
                const match = response.data;

                setName(match.name);
                setMatchDate(match.matchDate);
                setMatchTime(match.matchTime);
                setLocation(match.location);
                setReferee(match.referee ? match.referee.id : '');
                setPlayer1(match.player1 ? match.player1.id : '');
                setPlayer1Score(match.player1Score);
                setPlayer2(match.player2 ? match.player2.id : '');
                setPlayer2Score(match.player2Score);

                const fetchUsers = async (role) => {
                    const userResponse = await axios.get(`http://localhost:8081/api/user/role/${role}`);
                    return userResponse.data;
                };

                setReferees(await fetchUsers('referee'));
                setPlayers(await fetchUsers('player'));
            } catch (err) {
                setError(err.response?.data || 'Failed to fetch match details.');
            }
        };

        if (matchId) {
            fetchMatchDetails();
        }
    }, [matchId]);

    const handleUpdate = async () => {
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
                player2Score,
            };

            await axios.put(`http://localhost:8081/api/match/${matchId}`, matchDTO);
            handleClose(true);
        } catch (err) {
            setError(err.response?.data || 'Failed to update match.');
        }
    };

    return (
        <Dialog open={open} onClose={() => handleClose(false)} sx={{ '& .MuiPaper-root': { backgroundColor: '#f1f8e9' } }}>
            <DialogTitle>Update Match</DialogTitle>
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
                    InputLabelProps={{ shrink: true }}
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
                    InputLabelProps={{ shrink: true }}
                />

                <TextField
                    margin="dense"
                    label="Location"
                    required
                    type="text"
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
                </FormControl >
                <TextField
                    margin="dense"
                    label="Player 2 Score"
                    type="number"
                    fullWidth
                    variant="outlined"
                    value={player2Score}
                    onChange={(e) => setPlayer2Score(e.target.value)}
                />
            </DialogContent >
            <DialogActions>
                <Button onClick={() => handleClose(false)} color="secondary">
                    Cancel
                </Button>
                <Button onClick={handleUpdate} color="primary">
                    Update
                </Button>
            </DialogActions>
        </Dialog >
    );
};

export default UpdateMatchDialog;

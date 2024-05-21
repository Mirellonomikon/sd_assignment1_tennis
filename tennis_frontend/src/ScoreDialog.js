import React, { useState, useEffect } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    TextField,
    Button,
    DialogActions,
    Alert,
} from '@mui/material';
import axios from 'axios';

const ScoreDialog = ({ open, handleClose, matchId }) => {
    const [player1Score, setPlayer1Score] = useState('');
    const [player2Score, setPlayer2Score] = useState('');
    const [error, setError] = useState('');
    const [player1Name, setPlayer1Name] = useState('');
    const [player2Name, setPlayer2Name] = useState('');
    const [initialPlayer1Score, setInitialPlayer1Score] = useState('');
    const [initialPlayer2Score, setInitialPlayer2Score] = useState('');
    const token = localStorage.getItem('token');

    useEffect(() => {
        if (open && matchId) {
            console.log("Fetching details for match ID:", matchId);
            const fetchMatchDetails = async () => {
                try {
                    const response = await axios.get(`http://localhost:8081/api/match/matchId`, {
                        headers: { Authorization: `Bearer ${token}` },
                        params: { id: matchId }
                    });
                    const match = response.data;
                    const p1Score = match.player1Score != null ? match.player1Score.toString() : '0';
                    const p2Score = match.player2Score != null ? match.player2Score.toString() : '0';
                    
                    setPlayer1Score(p1Score);
                    setPlayer2Score(p2Score);
                    setInitialPlayer1Score(p1Score);
                    setInitialPlayer2Score(p2Score);
                    
                    setPlayer1Name(match.player1 ? match.player1.name : '');
                    setPlayer2Name(match.player2 ? match.player2.name : '');
                } catch (err) {
                    setError(err.response?.data || 'Failed to fetch match details.');
                }
            };

            fetchMatchDetails();
        } else {
            setError('');
            setPlayer1Name('');
            setPlayer2Name('');
        }
    }, [open, matchId]);

    const handleUpdateScore = async () => {
        try {
            const player1ScoreValue = parseInt(player1Score, 10);
            const player2ScoreValue = parseInt(player2Score, 10);

            const scoreData = {
                player1Score: player1ScoreValue,
                player2Score: player2ScoreValue,
            };

            await axios.put(`http://localhost:8081/api/match/match/score`, scoreData, {
                headers: { Authorization: `Bearer ${token}` },
                params: { matchId: matchId }
            });

            handleClose(true);
        } catch (err) {
            setError(err.response?.data || err.message || 'Failed to update scores.');
        }
    };

    const handleReset = () => {
        setPlayer1Score(initialPlayer1Score);
        setPlayer2Score(initialPlayer2Score);
    };

    return (
        <Dialog open={open} onClose={() => { handleClose(false); handleReset(); }} sx={{ '& .MuiPaper-root': { backgroundColor: '#f1f8e9' } }}>
            <DialogTitle>Update Scores</DialogTitle>
            <DialogContent>
                {error && (
                    <Alert severity="error" style={{ backgroundColor: '#FFF6EA', marginBottom: '5px' }}>
                        {error}
                    </Alert>
                )}
                <TextField
                    autoFocus
                    margin="dense"
                    label={`${player1Name} Score`}
                    type="number"
                    fullWidth
                    variant="outlined"
                    value={player1Score}
                    onChange={(e) => setPlayer1Score(e.target.value)}
                />
                <TextField
                    margin="dense"
                    label={`${player2Name} Score`}
                    type="number"
                    fullWidth
                    variant="outlined"
                    value={player2Score}
                    onChange={(e) => setPlayer2Score(e.target.value)}
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={() => handleClose(false)} color="secondary">
                    Cancel
                </Button>
                <Button onClick={handleUpdateScore} color="primary">
                    Update
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default ScoreDialog;

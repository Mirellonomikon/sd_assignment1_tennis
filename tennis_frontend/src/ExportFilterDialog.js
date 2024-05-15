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

const ExportFilterDialog = ({ open, handleClose }) => {
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const [location, setLocation] = useState('');
    const [referees, setReferees] = useState([]);
    const [players, setPlayers] = useState([]);
    const [selectedReferee, setSelectedReferee] = useState('');
    const [selectedPlayer, setSelectedPlayer] = useState('');
    const [format, setFormat] = useState('csv');
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchRefereesAndPlayers = async () => {
            try {
                const refereesResponse = await axios.get('http://localhost:8081/api/user/role/referee');
                const playersResponse = await axios.get('http://localhost:8081/api/user/role/player');
                setReferees(refereesResponse.data);
                setPlayers(playersResponse.data);
            } catch (err) {
                setError(`Failed to fetch data: ${err.response?.data || err.message}`);
            }
        };

        fetchRefereesAndPlayers();
    }, []);

    const resetFilters = () => {
        setStartDate('');
        setEndDate('');
        setLocation('');
        setSelectedReferee('');
        setSelectedPlayer('');
        setFormat('csv');
        setError(null);
    };

    const handleDialogClose = () => {
        resetFilters();
        handleClose();
    };

    const handleExport = async () => {
        const params = {
            format,
            startDate: startDate || undefined,
            endDate: endDate || undefined,
            location: location || undefined,
            refereeId: selectedReferee || undefined,
            playerId: selectedPlayer || undefined,
        };

        try {
            const response = await axios.get(
                'http://localhost:8081/api/match/export',
                {
                    params,
                    responseType: 'blob',
                }
            );

            const blob = new Blob([response.data], {
                type: format === 'csv' ? 'text/csv' : 'text/plain',
            });
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `matches.${format}`;
            a.click();
            window.URL.revokeObjectURL(url);

            handleDialogClose();
        } catch (err) {
            setError(`Failed to export matches: ${err.response?.data || err.message}`);
        }
    };

    return (
        <Dialog open={open} onClose={handleDialogClose} sx={{ '& .MuiPaper-root': { backgroundColor: '#f1f8e9' } }}>
            <DialogTitle>Filter & Export Matches</DialogTitle>
            <DialogContent>
                {error && <Alert severity="error" style={{ backgroundColor: '#FFF6EA', marginBottom: '5px' }}>{error}</Alert>}
                <TextField
                    margin="dense"
                    label="Start Date"
                    type="date"
                    fullWidth
                    variant="outlined"
                    value={startDate}
                    onChange={(e) => setStartDate(e.target.value)}
                    InputLabelProps={{
                        shrink: true,
                    }}
                />
                <TextField
                    margin="dense"
                    label="End Date"
                    type="date"
                    fullWidth
                    variant="outlined"
                    value={endDate}
                    onChange={(e) => setEndDate(e.target.value)}
                    InputLabelProps={{
                        shrink: true,
                    }}
                />
                <TextField
                    margin="dense"
                    label="Location"
                    type="text"
                    fullWidth
                    variant="outlined"
                    value={location}
                    onChange={(e) => setLocation(e.target.value)}
                />
                <FormControl fullWidth margin="dense">
                    <InputLabel>Referee</InputLabel>
                    <Select
                        value={selectedReferee}
                        label="Referee"
                        onChange={(e) => setSelectedReferee(e.target.value)}
                    >
                        <MenuItem value="">
                            <em>None</em>
                        </MenuItem>
                        {referees.map((referee) => (
                            <MenuItem key={referee.id} value={referee.id}>
                                {referee.name}
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>
                <FormControl fullWidth margin="dense">
                    <InputLabel>Player</InputLabel>
                    <Select
                        value={selectedPlayer}
                        label="Player"
                        onChange={(e) => setSelectedPlayer(e.target.value)}
                    >
                        <MenuItem value="">
                            <em>None</em>
                        </MenuItem>
                        {players.map((player) => (
                            <MenuItem key={player.id} value={player.id}>
                                {player.name}
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>
                <FormControl fullWidth margin="dense">
                    <InputLabel>Export Format</InputLabel>
                    <Select
                        value={format}
                        label="Export Format"
                        onChange={(e) => setFormat(e.target.value)}
                    >
                        <MenuItem value="csv">CSV</MenuItem>
                        <MenuItem value="txt">TXT</MenuItem>
                    </Select>
                </FormControl>
            </DialogContent>
            <DialogActions>
                <Button onClick={handleDialogClose} color="secondary">
                    Cancel
                </Button>
                <Button onClick={handleExport} color="primary">
                    Export
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default ExportFilterDialog;

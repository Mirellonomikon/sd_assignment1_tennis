import React, { useEffect, useState } from 'react';
import {
    Container,
    Button,
    Typography,
    Box,
    Alert,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableRow,
    TableContainer,
    Paper,
    Toolbar,
    TablePagination,
    TableSortLabel,
    IconButton,
    ClickAwayListener,
} from '@mui/material';
import { Edit } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import ScoreDialog from './ScoreDialog';

const RefereeSchedule = () => {
    const [matches, setMatches] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [sortField, setSortField] = useState('id');
    const [sortDirection, setSortDirection] = useState('asc');
    const [selectedMatch, setSelectedMatch] = useState(null);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [isScoreDialogOpen, setIsScoreDialogOpen] = useState(false);

    const navigate = useNavigate();
    const storedUser = localStorage.getItem('user');
    const refereeId = storedUser ? JSON.parse(storedUser).id : null;

    const fetchMatches = async () => {
        if (!refereeId) {
            setError('No referee ID found. Please log in again.');
            setLoading(false);
            return;
        }

        try {
            const response = await axios.get(`http://localhost:8081/api/match/ref/${refereeId}`);
            const data = response.data.map((match) => ({
                ...match,
                matchDate: match.matchDate ? new Date(match.matchDate).toLocaleDateString('en-GB') : 'N/A',
                matchTime: match.matchTime
                    ? new Date(`1970-01-01T${match.matchTime}`).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
                    : 'N/A',
            }));
            setMatches(data);
        } catch (err) {
            setError(`Failed to fetch matches: ${err.response?.data || err.message}`);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchMatches();
    }, [refereeId]);

    const handleRowClick = (match) => {
        if (match) {
            const isCurrentlySelected = selectedMatch?.id === match.id;
            setSelectedMatch(isCurrentlySelected ? null : match);
        }
    };

    const handleOpenScoreDialog = () => {
        if (selectedMatch) {
            setIsScoreDialogOpen(true);
        }
    };

    const handleCloseScoreDialog = () => {
        setIsScoreDialogOpen(false);
        fetchMatches(); // Refresh data
    };

    const handleSort = (field) => {
        const isAsc = sortField === field && sortDirection === 'asc';
        setSortField(field);
        setSortDirection(isAsc ? 'desc' : 'asc');
    };

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleLogout = () => {
        localStorage.removeItem('user');
        navigate('/login');
    };

    const sortedMatches = matches.sort((a, b) => {
        const aField = a[sortField] || '';
        const bField = b[sortField] || '';
        return sortDirection === 'asc' ? (aField > bField ? 1 : -1) : (aField < bField ? -1 : 1);
    });

    return (
        <Container component="main">
            <Box
                sx={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    marginTop: 2,
                    marginBottom: 2,
                }}
            >
                <Typography variant="h4">Referee Schedule</Typography>
                <Button variant="contained" color="primary">
                    Edit User
                </Button>
            </Box>

            {error && (
                <Alert severity="error" sx={{ marginBottom: 2 }}>
                    {error}
                </Alert>
            )}

            {loading ? (
                <Typography variant="body1">Loading...</Typography>
            ) : (
                <ClickAwayListener onClickAway={() => handleRowClick(null)}>
                    <TableContainer component={Paper}>
                        <Toolbar>
                            <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
                                <IconButton
                                    color="primary"
                                    onClick={handleOpenScoreDialog}
                                    disabled={!selectedMatch}
                                >
                                    <Edit />
                                </IconButton>
                            </Box>
                        </Toolbar>

                        <Table size="small" stickyHeader>
                            <TableHead>
                                <TableRow>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortField === 'id'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('id')}
                                        >
                                            ID
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortField === 'name'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('name')}
                                        >
                                            Match Name
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortField === 'matchDate'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('matchDate')}
                                        >
                                            Match Date
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortField === 'matchTime'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('matchTime')}
                                        >
                                            Match Time
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>Location</TableCell>
                                    <TableCell>Player 1</TableCell>
                                    <TableCell>Player 2</TableCell>
                                    <TableCell>Player 1 Score</TableCell>
                                    <TableCell>Player 2 Score</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {sortedMatches.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map((match) => (
                                    <TableRow
                                        key={match.id}
                                        hover
                                        onClick={() => handleRowClick(match)}
                                        selected={selectedMatch?.id === match.id}
                                    >
                                        <TableCell>{match.id}</TableCell>
                                        <TableCell>{match.name}</TableCell>
                                        <TableCell>{match.matchDate}</TableCell>
                                        <TableCell>{match.matchTime}</TableCell>
                                        <TableCell>{match.location}</TableCell>
                                        <TableCell>{match.player1?.name ?? 'N/A'}</TableCell>
                                        <TableCell>{match.player2?.name ?? 'N/A'}</TableCell>
                                        <TableCell>{match.player1Score ?? 'N/A'}</TableCell>
                                        <TableCell>{match.player2Score ?? 'N/A'}</TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>

                        <TablePagination
                            component="div"
                            count={(matches.length)}
                            page={page}
                            onPageChange={handleChangePage}
                            rowsPerPage={rowsPerPage}
                            onRowsPerPageChange={(e) => setRowsPerPage(parseInt(e.target.value, 10))}
                            rowsPerPageOptions={[10, 25, 50]}
                        />
                    </TableContainer>
                </ClickAwayListener>
            )}

            <ScoreDialog
                open={isScoreDialogOpen}
                handleClose={handleCloseScoreDialog}
                matchId={selectedMatch?.id}
            />

            <Box sx={{ display: 'flex', justifyContent: 'flex-start', marginTop: 2 }}>
                <Button variant="contained" color="secondary" onClick={handleLogout}>
                    Logout
                </Button>
            </Box>
        </Container>
    );
};

export default RefereeSchedule;

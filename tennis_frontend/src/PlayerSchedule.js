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
    ClickAwayListener,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import UpdateCredsForm from './UpdateCredsForm';

const PlayerSchedule = () => {
    const [matches, setMatches] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [sortField, setSortField] = useState('id');
    const [sortDirection, setSortDirection] = useState('asc');
    const [selectedMatch, setSelectedMatch] = useState(null);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [openUpdateForm, setOpenUpdateForm] = useState(false);
    const [isTournamentJoined, setIsTournamentJoined] = useState(false);
    const [tournamentStatus, setTournamentStatus] = useState("");

    const navigate = useNavigate();
    const storedUser = localStorage.getItem('user');
    const userId = storedUser ? JSON.parse(storedUser).id : null;
    const userStatus = storedUser ? JSON.parse(storedUser).isRegisteredInTournament : false;
    const userTournamentStatus = storedUser ? JSON.parse(storedUser).tournamentRegistrationStatus : "";

    const fetchMatches = async () => {
        try {
            const response = await axios.get('http://localhost:8081/api/match/all');
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

    const fetchUserTournamentStatus = () => {
        setIsTournamentJoined(userStatus);
        setTournamentStatus(userTournamentStatus);
    };

    useEffect(() => {
        fetchMatches();
        fetchUserTournamentStatus();
    }, [userId]);

    const handleRowClick = (match) => {
        setSelectedMatch(selectedMatch?.id === match.id ? null : match);
    };

    const handleTableClickAway = () => {
        setSelectedMatch(null);
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

    const handleOpenUpdateForm = () => {
        setOpenUpdateForm(true);
    };

    const handleCloseUpdateForm = () => {
        setOpenUpdateForm(false);
    };

    const handleTournamentRequest = async () => {
        try {
            await axios.put(`http://localhost:8081/api/user/${userId}/request-tournament`);

            const updatedUser = { ...JSON.parse(storedUser), isRegisteredInTournament: false, tournamentRegistrationStatus: "PENDING" };
            localStorage.setItem('user', JSON.stringify(updatedUser));

            setIsTournamentJoined(false);
            setTournamentStatus("PENDING");
        } catch (err) {
            setError(`Failed to request tournament registration: ${err.response?.data || err.message}`);
        }
    };

    const handleTournamentQuit = async () => {
        try {
            const userMatches = matches.filter(
                (match) => match.player1?.id === userId || match.player2?.id === userId
            );

            for (const match of userMatches) {
                await axios.put(`http://localhost:8081/api/match/${match.id}/remove/${userId}`);
            }

            await axios.put(`http://localhost:8081/api/user/${userId}/quit-tournament`);

            const updatedUser = { ...JSON.parse(storedUser), isRegisteredInTournament: false, tournamentRegistrationStatus: "NONE" };
            localStorage.setItem('user', JSON.stringify(updatedUser));

            setIsTournamentJoined(false);
            setTournamentStatus("NONE");

            fetchMatches();
        } catch (err) {
            setError(`Failed to quit tournament: ${err.response?.data || err.message}`);
        }
    };

    const sortedMatches = matches.sort((a, b) => {
        const aField = a[sortField] || '';
        const bField = b[sortField] || '';

        return sortDirection === 'asc' ? (aField > bField ? 1 : -1) : (aField < bField ? 1 : -1);
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
                <Typography variant="h4">Player Schedule</Typography>
                <Button variant="contained" color="primary" onClick={handleOpenUpdateForm}>
                    Edit Credentials
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
                <ClickAwayListener onClickAway={handleTableClickAway}>
                    <TableContainer component={Paper}>
                        <Toolbar>
                            <Box sx={{ display: 'flex', justifyContent: 'flex-end', alignItems: 'center' }}>
                                <Button
                                    variant="contained"
                                    color={isTournamentJoined ? "secondary" : "primary"}
                                    onClick={isTournamentJoined ? handleTournamentQuit : handleTournamentRequest}
                                    disabled={tournamentStatus === "PENDING"}
                                >
                                    {isTournamentJoined ? "Quit Tournament" : "Join Tournament"}
                                </Button>
                                <Typography variant="body1" sx={{ marginLeft: 2 }}>
                                    Status: {tournamentStatus}
                                </Typography>
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
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortField === 'referee'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('referee')}
                                        >
                                            Referee
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortField === 'location'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('location')}
                                        >
                                            Location
                                        </TableSortLabel>
                                    </TableCell>
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
                                        <TableCell>{match.referee?.name ?? 'N/A'}</TableCell>
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
                            count={matches.length}
                            page={page}
                            onPageChange={handleChangePage}
                            rowsPerPage={rowsPerPage}
                            onRowsPerPageChange={(e) => setRowsPerPage(parseInt(e.target.value, 10))}
                            rowsPerPageOptions={[10, 25, 50]}
                        />
                    </TableContainer>
                </ClickAwayListener>
            )}

            <Box sx={{ display: 'flex', justifyContent: 'flex-start', marginTop: 2 }}>
                <Button variant="contained" color="secondary" onClick={handleLogout}>
                    Logout
                </Button>
            </Box>
            <UpdateCredsForm
                open={openUpdateForm}
                handleClose={handleCloseUpdateForm}
                userId={userId}
            />
        </Container>
    );
};

export default PlayerSchedule;

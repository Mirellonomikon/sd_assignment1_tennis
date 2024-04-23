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
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { Edit, Delete } from '@mui/icons-material';
import ClickAwayListener from '@mui/material/ClickAwayListener';
import AddMatchDialog from './AddMatchDialog';
import UpdateMatchDialog from './UpdateMatchDialog';

const AdminSchedule = () => {
    const [matches, setMatches] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [sortField, setSortField] = useState('id');
    const [sortDirection, setSortDirection] = useState('asc');
    const [selectedMatch, setSelectedMatch] = useState(null);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [isAddFormOpen, setIsAddFormOpen] = useState(false);
    const [isUpdateFormOpen, setIsUpdateFormOpen] = useState(false);
    const [selectedMatchId, setSelectedMatchId] = useState(null);
    const navigate = useNavigate();

    const fetchMatches = async () => {
        try {
            const response = await axios.get('http://localhost:8081/api/match/all');
            const data = response.data.map((match) => ({
                ...match,
                matchDate: match.matchDate
                    ? new Date(match.matchDate).toLocaleDateString('en-GB') : 'N/A',
                matchTime: match.matchTime ? new Date(`1970-01-01T${match.matchTime}`).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : 'N/A',
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
    }, []);

    const handleSort = (field) => {
        const isAsc = sortField === field && sortDirection === 'asc';
        setSortField(field);
        setSortDirection(isAsc ? 'desc' : 'asc');
    };

    const handleRowClick = (match) => {
        setSelectedMatch(selectedMatch?.id === match.id ? null : match);
    };

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleOpenAddForm = () => {
        setIsAddFormOpen(true);
    };

    const handleCloseAddForm = () => {
        setIsAddFormOpen(false);
        fetchMatches();
    };

    const handleOpenUpdateForm = (matchId) => {
        setSelectedMatchId(matchId);
        setIsUpdateFormOpen(true);
    };

    const handleCloseUpdateForm = () => {
        setIsUpdateFormOpen(false);
        fetchMatches();
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    const handleTableClickAway = () => {
        setSelectedMatch(null);
    };

    const sortedMatches = matches.sort((a, b) => {
        const aField = a[sortField] || '';
        const bField = b[sortField] || '';

        return sortDirection === 'asc' ? (aField > bField ? 1 : -1) : (aField < bField ? 1 : -1);
    });

    const isEditDeleteEnabled = Boolean(selectedMatch);

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
                <Typography variant="h4">Admin Schedule</Typography>
                <Button
                    variant="contained"
                    color="primary"
                    onClick={() => navigate("/admin-users-view")}
                >
                    Users Table
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
                            <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
                                <IconButton
                                    color="primary"
                                    onClick={() => handleOpenUpdateForm(selectedMatch?.id)}
                                    disabled={!isEditDeleteEnabled}
                                >
                                    <Edit />
                                </IconButton>
                                <IconButton
                                    color="error"
                                    onClick={() => console.log('Delete')}
                                    disabled={!isEditDeleteEnabled}
                                >
                                    <Delete />
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
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortField === 'location'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('location')}
                                        >
                                            Location
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
                                            active={sortField === 'player1'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('player1')}
                                        >
                                            Player 1
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortField === 'player2'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('player2')}
                                        >
                                            Player 2
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>Player 1 Score</TableCell>
                                    <TableCell>Player 2 Score</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {sortedMatches
                                    .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                                    .map((match) => (
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
                                            <TableCell>
                                                {match.referee?.name || 'No Referee Assigned'}
                                            </TableCell>
                                            <TableCell>{match.player1?.name || 'No Player Assigned'}</TableCell>
                                            <TableCell>{match.player2?.name || 'No Player Assigned'}</TableCell>
                                            <TableCell>
                                                {match.player1Score !== null ? match.player1Score : 'N/A'}
                                            </TableCell>
                                            <TableCell>
                                                {match.player2Score !== null ? match.player2Score : 'N/A'}
                                            </TableCell>
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
                            onRowsPerPageChange={handleChangeRowsPerPage}
                            rowsPerPageOptions={[10, 25, 50]}
                            labelRowsPer Page
                        />
                    </TableContainer>
                </ClickAwayListener>
            )}

            <Box sx={{ display: 'flex', justifyContent: 'flex-end', marginTop: 2 }}>
                <Button
                    variant="contained"
                    color="primary"
                    onClick={() => console.log('Filter')}
                    sx={{ mr: 1 }}
                >
                    Filter & Export
                </Button>
                <Button
                    variant="contained"
                    color="secondary"
                    onClick={() => handleOpenAddForm()}
                >
                    Add Match
                </Button>
            </Box>
            <AddMatchDialog open={isAddFormOpen} handleClose={handleCloseAddForm} />
            <UpdateMatchDialog
                open={isUpdateFormOpen}
                matchId={selectedMatchId}
                handleClose={handleCloseUpdateForm}
                onUpdate={fetchMatches}
            />
        </Container>
    );
};

export default AdminSchedule;

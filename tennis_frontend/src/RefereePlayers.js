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
    TablePagination,
    TableSortLabel,
    ClickAwayListener,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import UpdateCredsForm from './UpdateCredsForm';
import FilterUsersForm from './FilterUsersForm';

const RefereePlayers = () => {
    const [players, setPlayers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [sortField, setSortField] = useState('id');
    const [sortDirection, setSortDirection] = useState('asc');
    const [selectedPlayer, setSelectedPlayer] = useState(null);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [openUpdateForm, setOpenUpdateForm] = useState(false);
    const [isFilterDialogOpen, setIsFilterDialogOpen] = useState(false);

    const navigate = useNavigate();
    const storedUser = localStorage.getItem('user');
    const refereeId = storedUser ? JSON.parse(storedUser).id : null;
    const token = localStorage.getItem('token');

    const fetchPlayers = async () => {
        try {
            const response = await axios.get('http://localhost:8081/api/user/role/player', {
                headers: { Authorization: `Bearer ${token}` }
            });
            setPlayers(response.data);
        } catch (err) {
            setError(`Failed to fetch players: ${err.response?.data || err.message}`);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchPlayers();
    }, []);

    const handleRowClick = (player) => {
        setSelectedPlayer(selectedPlayer?.id === player.id ? null : player);
    };

    const handleTableClickAway = () => {
        setSelectedPlayer(null);
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
        localStorage.removeItem('token');
        navigate('/login');
    };

    const handleOpenUpdateForm = () => {
        setOpenUpdateForm(true);
    };

    const handleCloseUpdateForm = () => {
        setOpenUpdateForm(false);
    };

    const handleSwitchView = () => {
        navigate('/referee-schedule');
    };

    const handleOpenFilterDialog = () => {
        setIsFilterDialogOpen(true);
    };

    const handleCloseFilterDialog = () => {
        setIsFilterDialogOpen(false);
    };

    const sortedPlayers = players.sort((a, b) => {
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
                <Typography variant="h4">Players</Typography>
                <Button variant="contained" color="primary" onClick={handleSwitchView}>
                    View Matches
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
                                            active={sortField === 'username'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('username')}
                                        >
                                            Username
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortField === 'name'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('name')}
                                        >
                                            Name
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortField === 'email'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('email')}
                                        >
                                            Email
                                        </TableSortLabel>
                                    </TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {sortedPlayers.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map((player) => (
                                    <TableRow
                                        key={player.id}
                                        hover
                                        onClick={() => handleRowClick(player)}
                                        selected={selectedPlayer?.id === player.id}
                                    >
                                        <TableCell>{player.id}</TableCell>
                                        <TableCell>{player.username}</TableCell>
                                        <TableCell>{player.name}</TableCell>
                                        <TableCell>{player.email}</TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>

                        <TablePagination
                            component="div"
                            count={players.length}
                            page={page}
                            onPageChange={handleChangePage}
                            rowsPerPage={rowsPerPage}
                            onRowsPerPageChange={(e) => setRowsPerPage(parseInt(e.target.value, 10))}
                            rowsPerPageOptions={[10, 25, 50]}
                        />
                    </TableContainer>
                </ClickAwayListener>
            )}

            <Box sx={{ display: 'flex', justifyContent: 'space-between', marginTop: 2 }}>
                <Button variant="contained" color="secondary" onClick={handleLogout}>
                    Logout
                </Button>

                <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1 }}>
                    <Button variant="contained" color="primary" onClick={handleOpenFilterDialog}>
                        Filter Players
                    </Button>

                    <Button variant="contained" color="primary" onClick={handleOpenUpdateForm}>
                        Edit Credentials
                    </Button>
                </Box>
            </Box>
            <FilterUsersForm
                open={isFilterDialogOpen}
                handleClose={handleCloseFilterDialog}
                setPlayers={setPlayers}
            />
            <UpdateCredsForm
                open={openUpdateForm}
                handleClose={handleCloseUpdateForm}
                userId={refereeId}
            />
        </Container>
    );
};

export default RefereePlayers;

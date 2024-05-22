package org.example.tennis_api.service;

import org.example.tennis_api.dto.match.MatchDTO;
import org.example.tennis_api.entity.Match;
import org.example.tennis_api.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.example.tennis_api.repository.MatchRepository;
import org.example.tennis_api.repository.UserRepository;
import org.example.tennis_api.mapper.MatchMapper;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class MatchServiceImplTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MatchMapper matchMapper;

    @InjectMocks
    private MatchServiceImpl matchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createMatch() throws Exception {
        MatchDTO matchDTO = new MatchDTO("matchName", LocalDate.now().plusDays(1), LocalTime.now(), "location", null, null, null, null, null);
        Match match = new Match();
        when(matchMapper.toEntity(matchDTO)).thenReturn(match);
        when(matchRepository.save(match)).thenReturn(match);

        Match result = matchService.createMatch(matchDTO);

        assertNotNull(result);
        verify(matchRepository).save(match);
    }

    @Test
    void registerPlayerToMatch() throws Exception {
        Match match = new Match();
        match.setId(1);
        User player = new User();
        player.setId(2);

        when(matchRepository.findById(1)).thenReturn(Optional.of(match));
        when(userRepository.findById(2)).thenReturn(Optional.of(player));
        when(matchRepository.save(any(Match.class))).thenReturn(match);

        Match result = matchService.registerPlayerToMatch(1, 2);

        assertNotNull(result);
        assertTrue(result.getPlayer1() == player || result.getPlayer2() == player);
        verify(matchRepository).save(match);
    }


    @Test
    void findAllMatches() {
        List<Match> matches = Collections.singletonList(new Match());
        when(matchRepository.findAll()).thenReturn(matches);

        List<Match> result = matchService.findAllMatches();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(matchRepository).findAll();
    }

    @Test
    void findAllMatchesByRefereeId() {
        List<Match> matches = Collections.singletonList(new Match());
        User referee = new User();
        referee.setId(1);
        referee.setUserType("referee");

        when(userRepository.findById(1)).thenReturn(Optional.of(referee));
        when(matchRepository.findByReferee(referee)).thenReturn(matches);

        List<Match> result = matchService.findAllMatchesByRefereeId(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(matchRepository).findByReferee(referee);
    }

    @Test
    void findAllMatchesByPlayerId() {
        List<Match> matches = Collections.singletonList(new Match());
        User player = new User();
        player.setId(1);
        player.setUserType("player");

        when(userRepository.findById(1)).thenReturn(Optional.of(player));
        when(matchRepository.findByPlayer1OrPlayer2(player, player)).thenReturn(matches);

        List<Match> result = matchService.findAllMatchesByPlayerId(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(matchRepository).findByPlayer1OrPlayer2(player, player);
    }

    @Test
    void findMatchById() {
        Match match = new Match();
        when(matchRepository.findById(1)).thenReturn(Optional.of(match));

        Match result = matchService.findMatchById(1);

        assertNotNull(result);
        verify(matchRepository).findById(1);
    }

    @Test
    void updateMatchScore() throws Exception {
        Match match = new Match();
        match.setId(1);

        when(matchRepository.findById(1)).thenReturn(Optional.of(match));
        when(matchRepository.save(match)).thenReturn(match);

        Match result = matchService.updateMatchScore(1, 2, 3);

        assertNotNull(result);
        assertEquals(2, result.getPlayer1Score());
        assertEquals(3, result.getPlayer2Score());
        verify(matchRepository).save(match);
    }

    @Test
    void updateMatch() throws Exception {
        MatchDTO matchDTO = new MatchDTO("matchName", LocalDate.now().plusDays(1), LocalTime.now(), "location", null, null, null, null, null);
        Match match = new Match();
        match.setId(1);

        when(matchRepository.findById(1)).thenReturn(Optional.of(match));
        when(matchRepository.save(match)).thenReturn(match);
        when(matchMapper.toEntity(matchDTO)).thenReturn(match);

        Match result = matchService.updateMatch(matchDTO, 1);

        assertNotNull(result);
        verify(matchRepository).save(match);
    }

    @Test
    void deleteMatch() throws Exception {
        when(matchRepository.existsById(1)).thenReturn(true);

        matchService.deleteMatch(1);

        verify(matchRepository).deleteById(1);
    }

    @Test
    void removePlayerFromMatch() throws Exception {
        Match match = new Match();
        match.setId(1);
        User player = new User();
        player.setId(1);
        match.setPlayer1(player);
        when(matchRepository.findById(1)).thenReturn(Optional.of(match));
        when(matchRepository.save(match)).thenReturn(match);  // Ensure save method is mocked to return the match

        Match result = matchService.removePlayerFromMatch(1, 1);

        assertNotNull(result);
        assertNull(result.getPlayer1());
        assertNull(result.getPlayer1Score());
        verify(matchRepository).save(match);
    }


    @Test
    void findMatchRef() {
        Match match = new Match();
        User referee = new User();
        referee.setId(1);
        match.setReferee(referee);
        when(matchRepository.findById(1)).thenReturn(Optional.of(match));

        Integer result = matchService.findMatchRef(1);

        assertNotNull(result);
        assertEquals(1, result);
    }

    @Test
    void findMatches() {
        List<Match> matches = Collections.singletonList(new Match());
        when(matchRepository.findAll()).thenReturn(matches);

        List<Match> result = matchService.findAllMatches();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(matchRepository).findAll();
    }
}
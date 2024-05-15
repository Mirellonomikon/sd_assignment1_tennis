package org.example.tennis_api.service;

import org.example.tennis_api.dto.match.MatchDTO;
import org.example.tennis_api.entity.Match;
import org.example.tennis_api.entity.User;
import org.example.tennis_api.mapper.MatchMapper;
import org.example.tennis_api.repository.MatchRepository;
import org.example.tennis_api.repository.UserRepository;
import org.example.tennis_api.utilities.MatchExportStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class MatchServiceImpl implements MatchService{

    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final MatchMapper matchMapper;

    @Autowired
    public MatchServiceImpl(MatchRepository matchRepository, UserRepository userRepository, MatchMapper matchMapper) {
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
        this.matchMapper = matchMapper;
    }

    private void validateMatchDetails(String name, LocalDate matchDate, LocalTime matchTime, String location) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Match name cannot be empty.");
        }
        if (matchDate == null) {
            throw new IllegalArgumentException("Match date cannot be null.");
        }
        if (matchTime == null) {
            throw new IllegalArgumentException("Match time cannot be null.");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be empty.");
        }
    }

    private void validateMatchDTO(MatchDTO matchDTO) throws IllegalArgumentException {
        if (matchDTO.getMatchDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Match date cannot be in the past.");
        }
        if (matchDTO.getPlayer1() != null && matchDTO.getPlayer2() != null) {
            if (matchDTO.getPlayer1().equals(matchDTO.getPlayer2())) {
                throw new IllegalArgumentException("Player1 and Player2 cannot be the same.");
            }
        }
    }

    @Override
    public Match createMatch(MatchDTO matchDTO) throws IllegalArgumentException {
        validateMatchDetails(matchDTO.getName(), matchDTO.getMatchDate(), matchDTO.getMatchTime(), matchDTO.getLocation());
        validateMatchDTO(matchDTO);

        Match match = matchMapper.toEntity(matchDTO);

        if (matchDTO.getReferee() != null) {
            User referee = userRepository.findById(matchDTO.getReferee())
                    .orElseThrow(() -> new IllegalArgumentException("Referee not found with ID: " + matchDTO.getReferee()));
            match.setReferee(referee);
        }

        if (matchDTO.getPlayer1() != null) {
            User player1 = userRepository.findById(matchDTO.getPlayer1())
                    .orElseThrow(() -> new IllegalArgumentException("Player 1 not found with ID: " + matchDTO.getPlayer1()));
            match.setPlayer1(player1);
        }

        if (matchDTO.getPlayer2() != null) {
            User player2 = userRepository.findById(matchDTO.getPlayer2())
                    .orElseThrow(() -> new IllegalArgumentException("Player 2 not found with ID: " + matchDTO.getPlayer2()));
            match.setPlayer2(player2);
        }

        return matchRepository.save(match);
    }


    @Override
    public Match registerPlayerToMatch(Integer matchId, Integer playerId) throws IllegalArgumentException, NoSuchElementException {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new NoSuchElementException("Match not found"));
        if (match.getPlayer1() != null && match.getPlayer2() != null) {
            throw new IllegalArgumentException("Match already has two players");
        }
        if ((match.getPlayer1() != null && match.getPlayer1().getId().equals(playerId)) ||
                (match.getPlayer2() != null && match.getPlayer2().getId().equals(playerId))) {
            throw new IllegalArgumentException("Player already registered to this match");
        }

        User player = userRepository.findById(playerId)
                .orElseThrow(() -> new NoSuchElementException("Player not found with ID: " + playerId));

        if (match.getPlayer1() == null) {
            match.setPlayer1(player);
        } else if (match.getPlayer2() == null) {
            match.setPlayer2(player);
        }

        return matchRepository.save(match);
    }

    @Override
    public List<Match> findAllMatches() {
        return matchRepository.findAll();
    }

    @Override
    public List<Match> findAllMatchesByRefereeId(Integer refereeId) throws IllegalArgumentException, NoSuchElementException {
        User referee = userRepository.findById(refereeId).orElseThrow(() -> new NoSuchElementException("User doesn't exist."));
        if(!referee.getUserType().equals("referee"))
            throw new IllegalArgumentException("User is not a referee");
        return matchRepository.findByReferee(referee);
    }

    @Override
    public Match findMatchById(Integer matchId) {
        return matchRepository.findById(matchId).orElseThrow(() -> new NoSuchElementException("Match not found"));
    }

    @Override
    public Match updateMatchScore(Integer matchId, Integer player1Score, Integer player2Score) throws NoSuchElementException {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new NoSuchElementException("Match not found"));

        match.setPlayer1Score(player1Score);
        match.setPlayer2Score(player2Score);
        
        return matchRepository.save(match);
    }

    @Override
    public Match updateMatch(MatchDTO matchDTO, Integer id) throws NoSuchElementException {
        Match existingMatch = matchRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Match not found"));
        validateMatchDTO(matchDTO);
        validateMatchDetails(matchDTO.getName(), matchDTO.getMatchDate(), matchDTO.getMatchTime(), matchDTO.getLocation());

        if (matchDTO.getReferee() != null && (existingMatch.getReferee() == null || !matchDTO.getReferee().equals(existingMatch.getReferee().getId()))) {
            User referee = userRepository.findById(matchDTO.getReferee())
                    .orElseThrow(() -> new NoSuchElementException("Referee with ID " + matchDTO.getReferee() + " not found"));
            existingMatch.setReferee(referee);
        } else if (matchDTO.getReferee() == null) {
            existingMatch.setReferee(null);
        }

        if (matchDTO.getPlayer1() != null && (existingMatch.getPlayer1() == null || !matchDTO.getPlayer1().equals(existingMatch.getPlayer1().getId()))) {
            User player1 = userRepository.findById(matchDTO.getPlayer1())
                    .orElseThrow(() -> new NoSuchElementException("Player1 with ID " + matchDTO.getPlayer1() + " not found"));
            existingMatch.setPlayer1(player1);
        } else if (matchDTO.getPlayer1() == null) {
            existingMatch.setPlayer1(null);
        }

        if (matchDTO.getPlayer2() != null && (existingMatch.getPlayer2() == null || !matchDTO.getPlayer2().equals(existingMatch.getPlayer2().getId()))) {
            User player2 = userRepository.findById(matchDTO.getPlayer2())
                    .orElseThrow(() -> new NoSuchElementException("Player2 with ID " + matchDTO.getPlayer2() + " not found"));
            existingMatch.setPlayer2(player2);
        } else if (matchDTO.getPlayer2() == null) {
            existingMatch.setPlayer2(null);
        }

        existingMatch.setName(matchDTO.getName());
        existingMatch.setMatchDate(matchDTO.getMatchDate());
        existingMatch.setMatchTime(matchDTO.getMatchTime());
        existingMatch.setLocation(matchDTO.getLocation());
        existingMatch.setPlayer1Score(matchDTO.getPlayer1Score());
        existingMatch.setPlayer2Score(matchDTO.getPlayer2Score());

        return matchRepository.save(existingMatch);
    }


    @Override
    public void deleteMatch(Integer matchId) throws NoSuchElementException {
        if (!matchRepository.existsById(matchId)) {
            throw new NoSuchElementException("Match not found");
        }
        matchRepository.deleteById(matchId);
    }

    @Override
    public Match removePlayerFromMatch(Integer matchId, Integer playerId) throws NoSuchElementException, IllegalArgumentException {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new NoSuchElementException("Match not found with ID: " + matchId));

        if (match.getPlayer1() != null && match.getPlayer1().getId().equals(playerId)) {
            match.setPlayer1(null);
            match.setPlayer1Score(null);
        }
        if (match.getPlayer2() != null && match.getPlayer2().getId().equals(playerId)) {
            match.setPlayer2(null);
            match.setPlayer2Score(null);
        }

        return matchRepository.save(match);
    }

    @Override
    public List<Match> findMatches(LocalDate startDate, LocalDate endDate, String location, Integer refereeId, Integer playerId) {
        List<Match> matches = matchRepository.findAll();

        if (startDate != null && endDate != null) {
            matches = matches.stream()
                    .filter(match -> !match.getMatchDate().isBefore(startDate) && !match.getMatchDate().isAfter(endDate))
                    .collect(Collectors.toList());
        }
        if (location != null && !location.isEmpty()) {
            matches = matches.stream()
                    .filter(match -> match.getLocation().equalsIgnoreCase(location))
                    .collect(Collectors.toList());
        }
        if (refereeId != null) {
            matches = matches.stream()
                    .filter(match -> match.getReferee() != null && match.getReferee().getId().equals(refereeId))
                    .collect(Collectors.toList());
        }
        if (playerId != null) {
            matches = matches.stream()
                    .filter(match -> (match.getPlayer1() != null && match.getPlayer1().getId().equals(playerId)) ||
                            (match.getPlayer2() != null && match.getPlayer2().getId().equals(playerId)))
                    .collect(Collectors.toList());
        }

        return matches;
    }


    @Override
    public void exportMatches(List<Match> matches, OutputStream outputStream, MatchExportStrategy strategy) throws IOException {
        strategy.export(matches, outputStream);
    }
}

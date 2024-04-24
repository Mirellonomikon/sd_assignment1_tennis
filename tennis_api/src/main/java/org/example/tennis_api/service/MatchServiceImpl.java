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
import java.util.List;
import java.util.Optional;
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

    private void validateMatchDTO(MatchDTO matchDTO) throws Exception {
        if (matchDTO.getMatchDate().isBefore(LocalDate.now())) {
            throw new Exception("Match date cannot be in the past.");
        }
        if (matchDTO.getPlayer1() != null && matchDTO.getPlayer2() != null) {
            if (matchDTO.getPlayer1().equals(matchDTO.getPlayer2())) {
                throw new Exception("Player1 and Player2 cannot be the same.");
            }
        }
    }

    @Override
    public Match createMatch(MatchDTO matchDTO) throws Exception {
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
    public Match registerPlayerToMatch(Integer matchId, Integer playerId) throws Exception {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new Exception("Match not found"));
        if (match.getPlayer1() != null && match.getPlayer2() != null) {
            throw new Exception("Match already has two players");
        }
        if ((match.getPlayer1() != null && match.getPlayer1().getId().equals(playerId)) ||
                (match.getPlayer2() != null && match.getPlayer2().getId().equals(playerId))) {
            throw new Exception("Player already registered to this match");
        }

        User player = userRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found with ID: " + playerId));

        if (match.getPlayer1() == null) {
            match.setPlayer1(player);
        } else if (match.getPlayer2() == null) {
            match.setPlayer2(player);
        }

        return matchRepository.findById(matchId).orElseThrow(() -> new Exception("Failed to update match"));
    }

    @Override
    public List<Match> findAllMatches() {
        return matchRepository.findAll();
    }

    @Override
    public List<Match> findAllMatchesByRefereeId(Integer refereeId) throws Exception {
        User referee = userRepository.findById(refereeId).orElseThrow(() -> new IllegalArgumentException("User doesn't exist."));
        if(!referee.getUserType().equals("referee"))
            throw new Exception("User is not a referee");
        return matchRepository.findByReferee(referee);
    }

    @Override
    public Optional<Match> findMatchById(Integer matchId) {
        return matchRepository.findById(matchId);
    }

    @Override
    public void updateMatchScore(Integer matchId, Integer player1Score, Integer player2Score) throws Exception {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new Exception("Match not found"));

        match.setPlayer1Score(player1Score);
        match.setPlayer2Score(player2Score);
        
        matchRepository.save(match);
    }

    @Override
    public Match updateMatch(MatchDTO matchDTO, Integer id) throws Exception {
        Match existingMatch = matchRepository.findById(id)
                .orElseThrow(() -> new Exception("Match not found"));
        validateMatchDTO(matchDTO);

        if (matchDTO.getReferee() != null && (existingMatch.getReferee() == null || !matchDTO.getReferee().equals(existingMatch.getReferee().getId()))) {
            User referee = userRepository.findById(matchDTO.getReferee())
                    .orElseThrow(() -> new IllegalArgumentException("Referee with ID " + matchDTO.getReferee() + " not found"));
            existingMatch.setReferee(referee);
        } else if (matchDTO.getReferee() == null) {
            existingMatch.setReferee(null);
        }

        if (matchDTO.getPlayer1() != null && (existingMatch.getPlayer1() == null || !matchDTO.getPlayer1().equals(existingMatch.getPlayer1().getId()))) {
            User player1 = userRepository.findById(matchDTO.getPlayer1())
                    .orElseThrow(() -> new IllegalArgumentException("Player1 with ID " + matchDTO.getPlayer1() + " not found"));
            existingMatch.setPlayer1(player1);
        } else if (matchDTO.getPlayer1() == null) {
            existingMatch.setPlayer1(null);
        }

        if (matchDTO.getPlayer2() != null && (existingMatch.getPlayer2() == null || !matchDTO.getPlayer2().equals(existingMatch.getPlayer2().getId()))) {
            User player2 = userRepository.findById(matchDTO.getPlayer2())
                    .orElseThrow(() -> new IllegalArgumentException("Player2 with ID " + matchDTO.getPlayer2() + " not found"));
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
    public void deleteMatch(Integer matchId) throws Exception {
        if (!matchRepository.existsById(matchId)) {
            throw new Exception("Match not found");
        }
        matchRepository.deleteById(matchId);
    }

    @Override
    public List<Match> findMatches(LocalDate startDate, LocalDate endDate, String location, Integer refereeId, Integer playerId) {
        List<Match> matches = matchRepository.findAll();

        if (startDate != null && endDate != null) {
            matches = matches.stream()
                    .filter(match -> !match.getMatchDate().isBefore(startDate) && !match.getMatchDate().isAfter(endDate))
                    .collect(Collectors.toList());
        }
        if (location != null) {
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

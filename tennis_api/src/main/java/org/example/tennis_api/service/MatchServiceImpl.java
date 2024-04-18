package org.example.tennis_api.service;

import jakarta.transaction.Transactional;
import org.example.tennis_api.dto.match.MatchDTO;
import org.example.tennis_api.entity.Match;
import org.example.tennis_api.mapper.MatchMapper;
import org.example.tennis_api.repository.MatchRepository;
import org.example.tennis_api.utilities.MatchExportStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MatchServiceImpl implements MatchService{

    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;

    @Autowired
    public MatchServiceImpl(MatchRepository matchRepository, MatchMapper matchMapper) {
        this.matchRepository = matchRepository;
        this.matchMapper = matchMapper;
    }

    private void validateMatchDTO(MatchDTO matchDTO) throws Exception {
        if (matchDTO.getMatchDate().isBefore(LocalDate.now())) {
            throw new Exception("Match date cannot be in the past.");
        }
        if (matchDTO.getPlayer1().equals(matchDTO.getPlayer2())) {
            throw new Exception("Player1 and Player2 cannot be the same.");
        }
    }

    @Override
    @Transactional
    public Match createMatch(MatchDTO matchDTO) throws Exception {
        validateMatchDTO(matchDTO);
        Match match = matchMapper.toEntity(matchDTO);
        Match updatedMatch = matchRepository.save(match);
        matchRepository.updateReferee(updatedMatch.getId(), matchDTO.getReferee());
        matchRepository.updatePlayer1(updatedMatch.getId(), matchDTO.getPlayer1());
        matchRepository.updatePlayer2(updatedMatch.getId(), matchDTO.getPlayer2());
        return updatedMatch;
    }

    @Override
    @Transactional
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

        if (match.getPlayer1() == null && matchRepository.setPlayer1(matchId, playerId) == 0) {
            throw new Exception("Failed to register player 1");
        } else if (match.getPlayer2() == null && matchRepository.setPlayer2(matchId, playerId) == 0) {
            throw new Exception("Failed to register player 2");
        }

        return matchRepository.findById(matchId).orElseThrow(() -> new Exception("Failed to update match"));
    }

    @Override
    public List<Match> findAllMatches() {
        return matchRepository.findAll();
    }

    @Override
    public Optional<Match> findMatchById(Integer matchId) throws Exception {
        return matchRepository.findById(matchId);
    }

    @Override
    public Match updateMatchScore(Integer matchId, Integer player1Score, Integer player2Score) throws Exception {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new Exception("Match not found"));
        match.setPlayer1Score(player1Score);
        match.setPlayer2Score(player2Score);
        return matchRepository.save(match);
    }

    @Override
    public Match updateMatch(MatchDTO matchDTO, Integer id) throws Exception {
        Match existingMatch = matchRepository.findById(id)
                .orElseThrow(() -> new Exception("Match not found"));
        validateMatchDTO(matchDTO);

        if (matchDTO.getReferee() != null && !matchDTO.getReferee().equals(existingMatch.getReferee().getId())) {
            matchRepository.updateReferee(id, matchDTO.getReferee());
        }
        if (matchDTO.getPlayer1() != null && (existingMatch.getPlayer1() == null || !matchDTO.getPlayer1().equals(existingMatch.getPlayer1().getId()))) {
            matchRepository.updatePlayer1(id, matchDTO.getPlayer1());
        }
        if (matchDTO.getPlayer2() != null && (existingMatch.getPlayer2() == null || !matchDTO.getPlayer2().equals(existingMatch.getPlayer2().getId()))) {
            matchRepository.updatePlayer2(id, matchDTO.getPlayer2());
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
    public List<Match> findMatchesByDateRange(LocalDate startDate, LocalDate endDate) {
        return matchRepository.findByMatchDateBetween(startDate, endDate);
    }

    @Override
    public List<Match> findMatchesByLocation(String location) {
        return matchRepository.findByLocation(location);
    }

    @Override
    public List<Match> findMatchesByReferee(Integer refereeId) {
        return matchRepository.findByRefereeId(refereeId);
    }

    @Override
    public List<Match> findMatchesByPlayer(Integer playerId) {
        return matchRepository.findByPlayer1IdOrPlayer2Id(playerId, playerId);
    }


    @Override
    public List<Match> findMatches(LocalDate startDate, LocalDate endDate, String location, Integer refereeId, Integer playerId) {
        if (startDate != null && endDate != null) {
            return findMatchesByDateRange(startDate, endDate);
        } else if (location != null) {
            return findMatchesByLocation(location);
        } else if (refereeId != null) {
            return findMatchesByReferee(refereeId);
        } else if (playerId != null) {
            return findMatchesByPlayer(playerId);
        }
        return findAllMatches();
    }

    @Override
    public void exportMatches(List<Match> matches, OutputStream outputStream, MatchExportStrategy strategy) throws IOException {
        strategy.export(matches, outputStream);
    }

}

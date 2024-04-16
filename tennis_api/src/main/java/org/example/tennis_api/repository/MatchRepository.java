package org.example.tennis_api.repository;

import org.example.tennis_api.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Integer> {
    List<Match> findByRefereeId(Integer refereeId);
    List<Match> findByPlayer1IdOrPlayer2Id(Integer player1Id, Integer player2Id);
    List<Match> findByMatchDate(LocalDate matchDate);
    List<Match> findByMatchDateBetween(LocalDate startDate, LocalDate endDate);
}

package org.example.tennis_api.repository;

import org.example.tennis_api.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Integer> {
    List<Match> findByRefereeId(Integer refereeId);
    List<Match> findByPlayer1IdOrPlayer2Id(Integer player1Id, Integer player2Id);
    List<Match> findByMatchDateBetween(LocalDate startDate, LocalDate endDate);
    List<Match> findByLocation(String location);

    @Modifying
    @Query("UPDATE Match m SET m.referee.id = :refereeId WHERE m.id = :matchId")
    void updateReferee(Integer matchId, Integer refereeId);

    @Modifying
    @Query("UPDATE Match m SET m.player1.id = :player1Id WHERE m.id = :matchId")
    void updatePlayer1(Integer matchId, Integer player1Id);

    @Modifying
    @Query("UPDATE Match m SET m.player2.id = :player2Id WHERE m.id = :matchId")
    void updatePlayer2(Integer matchId, Integer player2Id);

    @Modifying
    @Query("UPDATE Match m SET m.player1.id = :playerId WHERE m.id = :matchId AND m.player1 IS NULL")
    int setPlayer1(Integer matchId, Integer playerId);

    @Modifying
    @Query("UPDATE Match m SET m.player2.id = :playerId WHERE m.id = :matchId AND m.player2 IS NULL")
    int setPlayer2(Integer matchId, Integer playerId);
}

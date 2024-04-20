package org.example.tennis_api.repository;

import org.example.tennis_api.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<Match, Integer> {

    @Modifying
    @Query("UPDATE Match m SET m.player1.id = :playerId WHERE m.id = :matchId AND m.player1 IS NULL")
    int setPlayer1(Integer matchId, Integer playerId);

    @Modifying
    @Query("UPDATE Match m SET m.player2.id = :playerId WHERE m.id = :matchId AND m.player2 IS NULL")
    int setPlayer2(Integer matchId, Integer playerId);
}

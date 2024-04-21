package org.example.tennis_api.repository;

import org.example.tennis_api.entity.Match;
import org.example.tennis_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Integer> {
    List<Match> findByReferee(User referee);
}

package org.example.tennis_api.repository;

import org.example.tennis_api.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<Match, Integer> {

}

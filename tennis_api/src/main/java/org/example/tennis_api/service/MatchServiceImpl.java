package org.example.tennis_api.service;

import org.example.tennis_api.mapper.MatchMapper;
import org.example.tennis_api.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MatchServiceImpl implements MatchService{

    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;

    @Autowired
    public MatchServiceImpl(MatchRepository matchRepository, MatchMapper matchMapper) {
        this.matchRepository = matchRepository;
        this.matchMapper = matchMapper;
    }
}

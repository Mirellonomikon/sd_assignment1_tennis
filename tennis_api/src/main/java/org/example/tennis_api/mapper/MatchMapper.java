package org.example.tennis_api.mapper;

import org.example.tennis_api.dto.match.MatchDTO;
import org.example.tennis_api.entity.Match;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MatchMapper implements GenericMapper<Match, MatchDTO> {

    private final ModelMapper modelMapper;

    @Autowired
    public MatchMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Match toEntity(MatchDTO matchDTO) {
        return modelMapper.map(matchDTO, Match.class);
    }

    @Override
    public MatchDTO toDTO(Match match) {
        return modelMapper.map(match, MatchDTO.class);
    }
}

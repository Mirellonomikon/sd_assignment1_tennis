package org.example.tennis_api.mapper;

import org.example.tennis_api.dto.match.MatchDTO;
import org.example.tennis_api.dto.user.UserDTO;
import org.example.tennis_api.dto.user.UserSignUpDTO;
import org.example.tennis_api.dto.user.UserUpdateCredentialsDTO;
import org.example.tennis_api.entity.Match;
import org.example.tennis_api.entity.User;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(UserDTO.class, User.class).addMappings(mapper -> mapper.skip(User::setId));

        modelMapper.typeMap(UserSignUpDTO.class, User.class).addMappings(mapper -> {
            mapper.skip(User::setUserType);
            mapper.skip(User::setId);
        });

        modelMapper.getConfiguration().setAmbiguityIgnored(true);

        Converter<UserUpdateCredentialsDTO, User> userCredentialsUpdateConverter = context -> {
            UserUpdateCredentialsDTO source = context.getSource();
            User destination = new User();
            destination.setUsername(source.getUsername());
            destination.setName(source.getName());
            destination.setPassword(source.getNewPassword());
            return destination;
        };

        modelMapper.createTypeMap(UserUpdateCredentialsDTO.class, User.class)
                .setConverter(userCredentialsUpdateConverter);

        modelMapper.typeMap(Match.class, MatchDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getReferee().getId(), MatchDTO::setReferee);
            mapper.map(src -> src.getPlayer1().getId(), MatchDTO::setPlayer1);
            mapper.map(src -> src.getPlayer2().getId(), MatchDTO::setPlayer2);
        });

        Converter<MatchDTO, Match> matchDtoToMatchConverter = context -> {
            MatchDTO source = context.getSource();
            Match destination = new Match();
            if (source != null) {
                destination.setName(source.getName());
                destination.setMatchDate(source.getMatchDate());
                destination.setMatchTime(source.getMatchTime());
                destination.setLocation(source.getLocation());
                destination.setPlayer1Score(source.getPlayer1Score());
                destination.setPlayer2Score(source.getPlayer2Score());
                destination.setReferee(null);
                destination.setPlayer1(null);
                destination.setPlayer2(null);
            }
            return destination;
        };

        modelMapper.createTypeMap(MatchDTO.class, Match.class).setConverter(matchDtoToMatchConverter);

        return modelMapper;
    }
}

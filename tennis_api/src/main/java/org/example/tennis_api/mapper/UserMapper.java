package org.example.tennis_api.mapper;

import org.example.tennis_api.dto.user.UserDTO;
import org.example.tennis_api.dto.user.UserSignInDTO;
import org.example.tennis_api.dto.user.UserSignUpDTO;
import org.example.tennis_api.dto.user.UserUpdateCredentialsDTO;
import org.example.tennis_api.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements GenericMapper<User, UserDTO>{

    private final ModelMapper modelMapper;

    @Autowired
    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public User toEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    @Override
    public UserDTO toDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public User signUpDtoToEntity(UserSignUpDTO dto) {
        User user = modelMapper.map(dto, User.class);
        user.setUserType(resolveUserType(dto.getUserTypeCode()));
        return user;
    }

    public User updateCredentialsDtoToEntity(UserUpdateCredentialsDTO dto) {
        return modelMapper.map(dto, User.class);
    }

    public User signInDtoToEntity(UserSignInDTO dto) {
        return modelMapper.map(dto, User.class);
    }

    private String resolveUserType(String code) {
        return switch (code) {
            case "admin" -> "administrator";
            case "ref" -> "referee";
            default -> "player";
        };
    }
}

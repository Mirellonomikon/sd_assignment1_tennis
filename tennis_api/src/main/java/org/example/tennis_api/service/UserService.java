package org.example.tennis_api.service;

import org.example.tennis_api.dto.user.UserDTO;
import org.example.tennis_api.dto.user.UserSignInDTO;
import org.example.tennis_api.dto.user.UserSignUpDTO;
import org.example.tennis_api.dto.user.UserUpdateCredentialsDTO;
import org.example.tennis_api.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(UserSignUpDTO userSignUpDTO) throws Exception;
    User loginUser(UserSignInDTO userSignInDTO) throws Exception;
    User updateUserCredentials(UserUpdateCredentialsDTO userUpdateCredentialsDTO, Integer id) throws Exception;
    Optional<User> findUserByUsername(String username);
    List<User> findUserByRole(String role);
    List<User> findAllUsers();
    User updateUser(UserDTO userDTO, Integer id) throws Exception;
    void deleteUser(Integer userId) throws Exception;
}

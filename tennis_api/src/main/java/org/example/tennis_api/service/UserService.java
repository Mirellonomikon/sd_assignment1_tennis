package org.example.tennis_api.service;

import org.example.tennis_api.dto.user.UserDTO;
import org.example.tennis_api.dto.user.UserSignInDTO;
import org.example.tennis_api.dto.user.UserSignUpDTO;
import org.example.tennis_api.dto.user.UserUpdateCredentialsDTO;
import org.example.tennis_api.entity.User;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface UserService {
    User registerUser(UserSignUpDTO userSignUpDTO) throws Exception;
    User loginUser(UserSignInDTO userSignInDTO) throws Exception;
    User updateUserCredentials(UserUpdateCredentialsDTO userUpdateCredentialsDTO, Integer id) throws Exception;
    Optional<User> findUserById(Integer id);
    List<User> findUserByRole(String role);
    List<User> findRegisteredPlayers();
    User quitTournamentUser(Integer id) throws Exception;
    User requestTournamentRegistration(Integer id) throws NoSuchElementException;
    List<User> findAllUsers();
    User addUser(UserDTO userDTO) throws Exception;
    User updateUser(UserDTO userDTO, Integer id);
    void deleteUser(Integer userId);
    List<User> filterUsers(String name, String username, Boolean isCompeting);
}

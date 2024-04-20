package org.example.tennis_api.service;

import org.example.tennis_api.dto.user.UserDTO;
import org.example.tennis_api.dto.user.UserSignInDTO;
import org.example.tennis_api.dto.user.UserSignUpDTO;
import org.example.tennis_api.dto.user.UserUpdateCredentialsDTO;
import org.example.tennis_api.entity.User;
import org.example.tennis_api.mapper.UserMapper;
import org.example.tennis_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(UserSignUpDTO userSignUpDTO) throws Exception {
        if (userRepository.findByUsername(userSignUpDTO.getUsername()).isPresent()) {
            throw new Exception("Username already exists.");
        }
        userSignUpDTO.setPassword(passwordEncoder.encode(userSignUpDTO.getPassword()));
        User user = userMapper.signUpDtoToEntity(userSignUpDTO);
        return userRepository.save(user);
    }

    @Override
    public User loginUser(UserSignInDTO userSignInDTO) throws Exception {
        User user = userRepository.findByUsername(userSignInDTO.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        if (!passwordEncoder.matches(userSignInDTO.getPassword(), user.getPassword())) {
            throw new Exception("Invalid password.");
        }
        return user;
    }

    @Override
    public User updateUserCredentials(UserUpdateCredentialsDTO userUpdateCredentialsDTO, Integer id) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        if (!passwordEncoder.matches(userUpdateCredentialsDTO.getOldPassword(), user.getPassword())) {
            throw new Exception("Invalid old password.");
        }
        user.setName(userUpdateCredentialsDTO.getName());
        user.setPassword(passwordEncoder.encode(userUpdateCredentialsDTO.getNewPassword()));
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> findUserByRole(String role) {
        return userRepository.findByUserType(role);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(UserDTO userDTO, Integer id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        existingUser.setUsername(userDTO.getUsername());
        existingUser.setName(userDTO.getName());
        existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        existingUser.setUserType(userDTO.getUserType());
        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found.");
        }
        userRepository.deleteById(userId);
    }
}

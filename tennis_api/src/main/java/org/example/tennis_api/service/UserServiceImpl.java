package org.example.tennis_api.service;

import org.example.tennis_api.dto.user.UserDTO;
import org.example.tennis_api.dto.user.UserSignInDTO;
import org.example.tennis_api.dto.user.UserSignUpDTO;
import org.example.tennis_api.dto.user.UserUpdateCredentialsDTO;
import org.example.tennis_api.entity.User;
import org.example.tennis_api.mapper.UserMapper;
import org.example.tennis_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
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
    public User registerUser(UserSignUpDTO userSignUpDTO) throws DataIntegrityViolationException {
        if (userRepository.findByUsername(userSignUpDTO.getUsername()).isPresent()) {
            throw new DataIntegrityViolationException("Username already exists.");
        }
        userSignUpDTO.setPassword(passwordEncoder.encode(userSignUpDTO.getPassword()));
        User user = userMapper.signUpDtoToEntity(userSignUpDTO);
        return userRepository.save(user);
    }

    @Override
    public User loginUser(UserSignInDTO userSignInDTO) throws NoSuchElementException, IllegalArgumentException {
        User user = userRepository.findByUsername(userSignInDTO.getUsername())
                .orElseThrow(() -> new NoSuchElementException("User not found."));
        if (!passwordEncoder.matches(userSignInDTO.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password.");
        }
        return user;
    }

    @Override
    public User updateUserCredentials(UserUpdateCredentialsDTO userUpdateCredentialsDTO, Integer id) throws NoSuchElementException, IllegalArgumentException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found."));
        if (!passwordEncoder.matches(userUpdateCredentialsDTO.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid old password.");
        }

        Optional<User> existingUser = userRepository.findByUsername(userUpdateCredentialsDTO.getUsername());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Username already in use by another account.");
        }

        Optional<User> existingName = userRepository.findByName(userUpdateCredentialsDTO.getName());
        if (existingName.isPresent() && !existingName.get().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Name already in use by another account.");
        }

        user.setUsername(userUpdateCredentialsDTO.getUsername());
        user.setName(userUpdateCredentialsDTO.getName());
        user.setPassword(passwordEncoder.encode(userUpdateCredentialsDTO.getNewPassword()));
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findUserById(Integer id) {
        return userRepository.findById(id);
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
    public User addUser(UserDTO userDTO) throws DataIntegrityViolationException {
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new DataIntegrityViolationException("Username already exists.");
        }
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User user = userMapper.toEntity(userDTO);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(UserDTO userDTO, Integer id) throws NoSuchElementException{
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found."));

        Optional<User> userWithSameUsername = userRepository.findByUsername(userDTO.getUsername());
        if (userWithSameUsername.isPresent() && !userWithSameUsername.get().getId().equals(id)) {
            throw new IllegalArgumentException("Username already in use by another account.");
        }

        Optional<User> userWithSameName = userRepository.findByName(userDTO.getName());
        if (userWithSameName.isPresent() && !userWithSameName.get().getId().equals(id)) {
            throw new IllegalArgumentException("Name already in use by another account.");
        }

        existingUser.setUsername(userDTO.getUsername());
        existingUser.setName(userDTO.getName());
        existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        existingUser.setUserType(userDTO.getUserType());
        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Integer userId) throws NoSuchElementException{
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User not found.");
        }
        userRepository.deleteById(userId);
    }
}

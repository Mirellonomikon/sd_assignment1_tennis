package org.example.tennis_api.service;

import org.example.tennis_api.dto.user.UserDTO;
import org.example.tennis_api.dto.user.UserSignInDTO;
import org.example.tennis_api.dto.user.UserSignUpDTO;
import org.example.tennis_api.dto.user.UserUpdateCredentialsDTO;
import org.example.tennis_api.entity.User;
import org.example.tennis_api.mapper.UserMapper;
import org.example.tennis_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser() {
        UserSignUpDTO signUpDTO = new UserSignUpDTO("username", "password", "email@example.com", "name", "");
        User user = new User();
        when(userRepository.findByUsername("username")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userMapper.signUpDtoToEntity(signUpDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.registerUser(signUpDTO);

        assertNotNull(result);
        verify(userRepository).save(user);
    }

    @Test
    void loginUser() {
        UserSignInDTO signInDTO = new UserSignInDTO("username", "password");
        User user = new User();
        user.setPassword("encodedPassword");
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        User result = userService.loginUser(signInDTO);

        assertNotNull(result);
    }

    @Test
    void updateUserCredentials() {
        UserUpdateCredentialsDTO updateDTO = new UserUpdateCredentialsDTO("newUsername", "newName", "email@example.com", "newPassword", "oldPassword");
        User user = new User();
        user.setId(1);
        user.setPassword("encodedOldPassword");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.findByUsername("newUsername")).thenReturn(Optional.empty());
        when(userRepository.findByName("newName")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUserCredentials(updateDTO, 1);

        assertNotNull(result);
        assertEquals("newUsername", result.getUsername());
        assertEquals("newName", result.getName());
        assertEquals("encodedNewPassword", result.getPassword());
        verify(userRepository).save(user);
        verify(passwordEncoder).matches("oldPassword", "encodedOldPassword");
        verify(passwordEncoder).encode("newPassword");
    }

    @Test
    void findUserById() {
        User user = new User();
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findUserById(1);

        assertTrue(result.isPresent());
    }

    @Test
    void findUserByUsername() {
        User user = new User();
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findUserByUsername("username");

        assertTrue(result.isPresent());
    }

    @Test
    void findUserByRole() {
        List<User> users = Collections.singletonList(new User());
        when(userRepository.findByUserType("player")).thenReturn(users);

        List<User> result = userService.findUserByRole("player");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findRegisteredPlayers() {
        List<User> users = Collections.singletonList(new User());
        when(userRepository.findByIsRegisteredInTournament(true)).thenReturn(users);

        List<User> result = userService.findRegisteredPlayers();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void quitTournamentUser() {
        User user = new User();
        user.setId(1);
        user.setIsRegisteredInTournament(true);
        user.setTournamentRegistrationStatus("PENDING");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.quitTournamentUser(1);

        assertNotNull(result);
        assertFalse(result.getIsRegisteredInTournament());
        assertEquals("NONE", result.getTournamentRegistrationStatus());
        verify(userRepository).save(user);
    }

    @Test
    void requestTournamentRegistration() {
        User user = new User();
        user.setId(1);
        user.setUserType("player");
        user.setTournamentRegistrationStatus("NONE");
        user.setIsRegisteredInTournament(false);
        user.setName("Test User");
        user.setUsername("testuser");

        User admin1 = new User();
        admin1.setEmail("admin1@example.com");
        User admin2 = new User();
        admin2.setEmail("admin2@example.com");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findByUserType("administrator")).thenReturn(Arrays.asList(admin1, admin2));

        User result = userService.requestTournamentRegistration(1);

        assertNotNull(result);
        assertEquals("PENDING", result.getTournamentRegistrationStatus());
        assertFalse(result.getIsRegisteredInTournament());
        verify(userRepository).save(user);
        verify(emailService).notifyAdmins(eq("New Tournament Registration Request"),
                contains("A new tournament registration request has been received from Test User (testuser)"),
                eq(Arrays.asList("admin1@example.com", "admin2@example.com")));
    }

    @Test
    void findAllUsers() {
        List<User> users = Collections.singletonList(new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.findAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void addUser() {
        UserDTO userDTO = new UserDTO("username", "password", "email@example.com", "name", "player", true, "PENDING");
        User user = new User();
        when(userRepository.findByUsername("username")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.addUser(userDTO);

        assertNotNull(result);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser() {
        UserDTO userDTO = new UserDTO("username", "password", "email@example.com", "name", "player", true, "PENDING");
        User existingUser = new User();
        existingUser.setId(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(existingUser));
        when(userRepository.findByName("name")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        User result = userService.updateUser(userDTO, 1);

        assertNotNull(result);
        verify(userRepository).save(existingUser);
    }

    @Test
    void deleteUser() {
        when(userRepository.existsById(1)).thenReturn(true);

        userService.deleteUser(1);

        verify(userRepository).deleteById(1);
    }

    @Test
    void filterUsers() {
        User user = new User();
        user.setId(1);
        user.setName("TestName");
        user.setUsername("TestUsername");
        user.setIsRegisteredInTournament(true);

        List<User> users = Collections.singletonList(user);
        when(userRepository.findByUserType("player")).thenReturn(users);

        List<User> result = userService.filterUsers("name", "username", true);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(user, result.getFirst());
    }

    @Test
    void acceptTournamentRegistration() {
        User user = new User();
        user.setId(1);
        user.setName("Test User");
        user.setUsername("TestUsername");
        user.setEmail("testuser@example.com");
        user.setIsRegisteredInTournament(false);
        user.setTournamentRegistrationStatus("PENDING");
        user.setUserType("player");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.acceptTournamentRegistration(1);

        assertNotNull(result);
        assertTrue(result.getIsRegisteredInTournament());
        assertEquals("ACCEPTED", result.getTournamentRegistrationStatus());
        verify(userRepository).save(user);
        verify(emailService).notifyUser(eq("testuser@example.com"), eq("Tournament Registration Accepted"),
                contains("Your registration for the tournament has been accepted"));
    }

    @Test
    void rejectTournamentRegistration() {
        User user = new User();
        user.setId(1);
        user.setIsRegisteredInTournament(true);
        user.setTournamentRegistrationStatus("PENDING");
        user.setName("Test User");
        user.setEmail("testuser@example.com");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.rejectTournamentRegistration(1);

        assertNotNull(result);
        assertFalse(result.getIsRegisteredInTournament());
        assertEquals("REJECTED", result.getTournamentRegistrationStatus());
        verify(userRepository).save(user);
        verify(emailService).notifyUser(eq("testuser@example.com"), eq("Tournament Registration Rejected"),
                contains("Your registration for the tournament has been rejected"));
    }


    @Test
    void findUserByStatus() {
        List<User> users = Collections.singletonList(new User());
        when(userRepository.findByTournamentRegistrationStatus("PENDING")).thenReturn(users);

        List<User> result = userService.findUserByStatus("PENDING");

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
package org.example.tennis_api.controller;

import lombok.RequiredArgsConstructor;
import org.example.tennis_api.dto.user.UserDTO;
import org.example.tennis_api.dto.user.UserSignInDTO;
import org.example.tennis_api.dto.user.UserSignUpDTO;
import org.example.tennis_api.dto.user.UserUpdateCredentialsDTO;
import org.example.tennis_api.entity.User;
import org.example.tennis_api.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserSignUpDTO userSignUpDTO) throws Exception {
        User registeredUser = userService.registerUser(userSignUpDTO);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody UserSignInDTO userSignInDTO) throws Exception {
        User user = userService.loginUser(userSignInDTO);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update")
    public ResponseEntity<User> updateCredentials(@RequestParam Integer id, @RequestBody UserUpdateCredentialsDTO userUpdateCredentialsDTO) throws Exception {
        User updatedUser = userService.updateUserCredentials(userUpdateCredentialsDTO, id);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUserByRole(@PathVariable String role) {
        List<User> users = userService.findUserByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role/tournament")
    public ResponseEntity<List<User>> getRegisteredPlayers() {
        List<User> users = userService.findRegisteredPlayers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/quit-tournament")
    public ResponseEntity<User> userTournamentStatus(@RequestParam Integer id) throws Exception {
        User updatedUser = userService.quitTournamentUser(id);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/request-tournament")
    public ResponseEntity<User> requestTournamentRegistration(@RequestParam Integer id) {
        User user = userService.requestTournamentRegistration(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/accept-tournament")
    public ResponseEntity<User> acceptTournamentRegistration(@RequestParam Integer id) {
        User updatedUser = userService.acceptTournamentRegistration(id);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/reject-tournament")
    public ResponseEntity<User> rejectTournamentRegistration(@RequestParam Integer id) {
        User updatedUser = userService.rejectTournamentRegistration(id);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<User>> getPendingUsers() {
        List<User> users = userService.findUserByStatus("PENDING");
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Integer id) throws IllegalArgumentException {
        User user = userService.findUserById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/add")
    public ResponseEntity<User> addUser(@RequestBody UserDTO userDTO) throws Exception {
        User registeredUser = userService.addUser(userDTO);
        return ResponseEntity.ok(registeredUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@RequestBody UserDTO userDTO, @PathVariable Integer id) {
        User updatedUser = userService.updateUser(userDTO, id);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) throws IllegalArgumentException {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/filter/players")
    public ResponseEntity<List<User>> filterUsers(@RequestParam(required = false) String name,
                                                  @RequestParam(required = false) String username,
                                                  @RequestParam(required = false) Boolean isCompeting) {
        List<User> users = userService.filterUsers(name, username, isCompeting);
        return ResponseEntity.ok(users);
    }
}

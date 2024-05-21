package org.example.tennis_api.controller;

import lombok.RequiredArgsConstructor;
import org.example.tennis_api.dto.user.UserDTO;
import org.example.tennis_api.dto.user.UserSignInDTO;
import org.example.tennis_api.dto.user.UserSignUpDTO;
import org.example.tennis_api.dto.user.UserUpdateCredentialsDTO;
import org.example.tennis_api.entity.User;
import org.example.tennis_api.security.JwtUtil;
import org.example.tennis_api.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    //permitted for all
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserSignUpDTO userSignUpDTO) throws Exception {
        User registeredUser = userService.registerUser(userSignUpDTO);
        return ResponseEntity.ok(registeredUser);
    }

//    @PostMapping("/login")
//    public ResponseEntity<User> loginUser(@RequestBody UserSignInDTO userSignInDTO) throws Exception {
//            User user = userService.loginUser(userSignInDTO);
//            return ResponseEntity.ok(user);
//    }

    //permitted for all
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody UserSignInDTO userSignInDTO) throws Exception {
            User user = userService.loginUser(userSignInDTO);
            String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getUserType());

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("token", token);
            responseBody.put("user", user);

            return ResponseEntity.ok(responseBody);
    }

    //permitted for all but checks for id to match token one
    @PutMapping("/update")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<User> updateCredentials(@RequestParam Integer id, @RequestBody UserUpdateCredentialsDTO userUpdateCredentialsDTO) throws Exception {
        User updatedUser = userService.updateUserCredentials(userUpdateCredentialsDTO, id);
        return ResponseEntity.ok(updatedUser);
    }

    //administrator only
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    //administrator only
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<List<User>> getUserByRole(@PathVariable String role) {
        List<User> users = userService.findUserByRole(role);
        return ResponseEntity.ok(users);
    }

    //administrator only
    @GetMapping("/role/tournament")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<List<User>> getRegisteredPlayers() {
        List<User> users = userService.findRegisteredPlayers();
        return ResponseEntity.ok(users);
    }

    //player only, but checks for id to match token one
    @PutMapping("/quit-tournament")
    @PreAuthorize("#id == authentication.principal.id and hasRole('PLAYER')")
    public ResponseEntity<User> userTournamentStatus(@RequestParam Integer id) throws Exception {
        User updatedUser = userService.quitTournamentUser(id);
        return ResponseEntity.ok(updatedUser);
    }

    //player only, but checks for id to match token one
    @PutMapping("/request-tournament")
    @PreAuthorize("#id == authentication.principal.id and hasRole('PLAYER')")
    public ResponseEntity<User> requestTournamentRegistration(@RequestParam Integer id) {
        User user = userService.requestTournamentRegistration(id);
        return ResponseEntity.ok(user);
    }

    //administrator only
    @PutMapping("/accept-tournament")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<User> acceptTournamentRegistration(@RequestParam Integer id) {
        User updatedUser = userService.acceptTournamentRegistration(id);
        return ResponseEntity.ok(updatedUser);
    }

    //administrator only
    @PutMapping("/reject-tournament")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<User> rejectTournamentRegistration(@RequestParam Integer id) {
        User updatedUser = userService.rejectTournamentRegistration(id);
        return ResponseEntity.ok(updatedUser);
    }

    //administrator only
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<List<User>> getPendingUsers() {
        List<User> users = userService.findUserByStatus("PENDING");
        return ResponseEntity.ok(users);
    }

    //allowed for all but for player and referee checks for id to match token one
    @GetMapping("/id")
    @PreAuthorize("hasRole('ADMINISTRATOR') or (#userId == authentication.principal.id)")
    public ResponseEntity<User> getUser(@RequestParam Integer userId) throws IllegalArgumentException {
        User user = userService.findUserById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ResponseEntity.ok(user);
    }

    //administrator only
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<User> addUser(@RequestBody UserDTO userDTO) throws Exception {
        User registeredUser = userService.addUser(userDTO);
        return ResponseEntity.ok(registeredUser);
    }

    //administrator only
    @PutMapping("/id")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<User> updateUser(@RequestBody UserDTO userDTO, @RequestParam Integer id) {
        User updatedUser = userService.updateUser(userDTO, id);
        return ResponseEntity.ok(updatedUser);
    }

    //administrator only
    @DeleteMapping("/id")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Void> deleteUser(@RequestParam Integer id) throws IllegalArgumentException {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    //referee and administrator only
    @GetMapping("/filter/players")
    @PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('REFEREE')")
    public ResponseEntity<List<User>> filterUsers(@RequestParam(required = false) String name,
                                                  @RequestParam(required = false) String username,
                                                  @RequestParam(required = false) Boolean isCompeting) {
        List<User> users = userService.filterUsers(name, username, isCompeting);
        return ResponseEntity.ok(users);
    }
}

package org.example.tennis_api.controller;

import lombok.RequiredArgsConstructor;
import org.example.tennis_api.dto.user.UserDTO;
import org.example.tennis_api.dto.user.UserSignInDTO;
import org.example.tennis_api.dto.user.UserSignUpDTO;
import org.example.tennis_api.dto.user.UserUpdateCredentialsDTO;
import org.example.tennis_api.entity.User;
import org.example.tennis_api.mapper.UserMapper;
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
    private final UserMapper userMapper;

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

    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateCredentials(@PathVariable Integer id, @RequestBody UserUpdateCredentialsDTO userUpdateCredentialsDTO) throws Exception {
        User updatedUser = userService.updateUserCredentials(userUpdateCredentialsDTO, id);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{role}")
    public ResponseEntity<List<User>> getUserByRole(@PathVariable String role) {
        List<User> users = userService.findUserByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{name}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String name) throws IllegalArgumentException {
        User user = userService.findUserByUsername(name).orElseThrow(() -> new IllegalArgumentException("User not found"));
        UserDTO userDTO = userMapper.toDTO(user);
        return ResponseEntity.ok(userDTO);
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
}

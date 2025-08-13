package com.example.authservice.controllers;

import com.example.authservice.DTO.UserDTO;
import com.example.authservice.impl.UserDetailsImpl;
import com.example.authservice.mappers.UserMapper;
import com.example.authservice.models.User;
import com.example.authservice.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UsersController {

    private static final Logger LOG = LoggerFactory.getLogger(UsersController.class);
    private final UserService userService;
    private final UserMapper userMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        LOG.info("Fetching all users (admin only)");
        List<UserDTO> users = userService.getAllUsers().stream()
                .map(userMapper::userToUserDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        LOG.info("Fetching user with ID: {}", id);
        User user = userService.getUserById(id);
        UserDTO userDTO = userMapper.userToUserDTO(user);
        LOG.info("Mapped UserDTO: username={}, firstName={}, lastName={}, role={}",
                userDTO.username(), userDTO.firstName(), userDTO.lastName(),userDTO.email(), userDTO.role());
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUserDTO() {
        UserDTO userDTO = userService.getCurrentUserDTO();
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody User updatedUser) {
        LOG.info("Updating user with ID: {}", id);
        User user = userService.updateUser(id, updatedUser);
        UserDTO userDTO = userMapper.userToUserDTO(user);
        LOG.info("Mapped UserDTO after update: username={}, firstName={}, lastName={}, role={}",
                userDTO.username(), userDTO.firstName(), userDTO.lastName(),userDTO.email(), userDTO.role());
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        LOG.info("Deleting user with ID: {}", id);
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUser();
    }
}

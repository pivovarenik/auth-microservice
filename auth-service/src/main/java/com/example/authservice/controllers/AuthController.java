package com.example.authservice.controllers;

import com.example.authservice.DTO.UserDTO;
import com.example.authservice.mappers.UserMapper;
import com.example.authservice.models.User;
import com.example.authservice.services.AuthorizationService;
import com.example.authservice.services.JwtCoreService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthorizationService userService;
    private final JwtCoreService jwtCore;
    private final Logger LOG = LoggerFactory.getLogger(AuthController.class);
    private final UserMapper userMapper;

    @PostMapping("/signUp")
    ResponseEntity<?> signUp(@Valid @RequestBody User input) {
        LOG.info("SignUp input: {}", input);
        User user = userService.registerUser(input);
        LOG.info("User after registration: {}", user);
        UserDTO userDTO = userMapper.userToUserDTO(user);
        LOG.info("Mapped UserDTO: {}", userDTO);
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    @PostMapping("/signIn")
    ResponseEntity<?> signIn(@RequestBody User input) {
        LOG.info("SignIn input: username={}", input.getUsername());
        User user = userService.verify(input);
        LOG.info("Verified user: username={}, firstName={}, lastName={}, role={}",
                user.getUsername(), user.getFirstName(), user.getLastName(), user.getRole());
        String token = jwtCore.generateToken(user);
        LOG.info("Generated JWT: {}", token);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}
package com.example.authservice.services;

import com.example.authservice.DTO.Event;
import com.example.authservice.enums.Option;
import com.example.authservice.enums.Role;
import com.example.authservice.exceptions.UserException;
import com.example.authservice.impl.UserDetailsImpl;
import com.example.authservice.models.User;
import com.example.authservice.repos.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AuthorizationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RabbitTemplate rabbitTemplate;
    private final Logger LOG = LoggerFactory.getLogger(AuthorizationService.class);

    public User registerUser(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            if (user.getRole().equals(Role.USER)) {
                Event event = new Event();
                event.setUsername(user.getUsername());
                event.setOption(Option.CREATED);
                event.setEmail(user.getEmail());
                event.setAddressees(userRepository.findByRole(Role.ADMIN).stream()
                        .map(User::getEmail)
                        .collect(Collectors.joining(",")));
                rabbitTemplate.convertAndSend("user-change-queue", event);
                LOG.info("Отправлено событие о создании нового пользователя");
            }
            return user;
        } catch (DataIntegrityViolationException e) {
            throw new UserException("User with this username or email already exists", e);
        } catch (Exception e) {
            throw new UserException("Failed to register user: " + e.getMessage(), e);
        }
    }

    public User verify(User input) throws NoSuchElementException {
        try{
            Authentication auth =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(input.getUsername(), input.getPassword()));
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            return userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new UserException("User not found after authentication"));
        }
        catch (AuthenticationException e){
            throw new UserException("Authentication failed: invalid username or password", e);
        }
    }
}


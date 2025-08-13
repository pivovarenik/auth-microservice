package com.example.authservice.services;


import com.example.authservice.DTO.Event;
import com.example.authservice.DTO.UserDTO;
import com.example.authservice.enums.Option;
import com.example.authservice.enums.Role;
import com.example.authservice.exceptions.UserException;
import com.example.authservice.impl.UserDetailsImpl;
import com.example.authservice.mappers.UserMapper;
import com.example.authservice.models.User;
import com.example.authservice.repos.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final Logger LOG = LoggerFactory.getLogger(UserService.class);
    private final RabbitTemplate rabbitTemplate;

    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUser();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        User currentUser = getCurrentUser();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found with ID: " + id));

        if (!currentUser.getRole().equals(Role.ADMIN) && !currentUser.getId().equals(id)) {
            throw new UserException("Access denied: You can only view your own profile");
        }
        return user;
    }

    public User updateUser(Long id, User updatedUser) {
        User currentUser = getCurrentUser();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found with ID: " + id));

        if (!currentUser.getRole().equals(Role.ADMIN) && !currentUser.getId().equals(id)) {
            throw new UserException("Access denied: You can only update your own profile");
        }
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());
        user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        if (currentUser.getRole().equals(Role.ADMIN)) {
            if (updatedUser.getUsername() != null) user.setUsername(updatedUser.getUsername());
            if (updatedUser.getRole() != null) user.setRole(updatedUser.getRole());
        }

        try {
            userRepository.save(user);
            if (user.getRole().equals(Role.USER)) {
                Event event = new Event();
                event.setUsername(user.getUsername());
                event.setOption(Option.CHANGED);
                event.setEmail(user.getEmail());
                event.setAddressees(adminEmails());
                sendMsg(event);
            }
            return user;
        } catch (DataIntegrityViolationException e) {
            throw new UserException("User with this username or email already exists", e);
        } catch (Exception e) {
            throw new UserException("Failed to update user: " + e.getMessage(), e);
        }
    }

    public void deleteUser(Long id) {
        User currentUser = getCurrentUser();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found with ID: " + id));

        if (!currentUser.getRole().equals(Role.ADMIN) && !currentUser.getId().equals(id)) {
            LOG.error("id = {}, currentUserID{}",id,currentUser.getId());
            throw new UserException("Access denied: You can only delete your own profile");
        }

        try {
            userRepository.delete(user);
            if (user.getRole().equals(Role.USER)) {
                Event event = new Event();
                event.setUsername(user.getUsername());
                event.setOption(Option.DELETED);
                event.setEmail(user.getEmail());
                event.setAddressees(adminEmails());
                sendMsg(event);
            }
        } catch (Exception e) {
            throw new UserException("Failed to delete user: " + e.getMessage(), e);
        }
    }
    public UserDTO getCurrentUserDTO(){
        User currentUser = getCurrentUser();
        UserDTO userDTO = userMapper.userToUserDTO(currentUser);
        return userDTO;
    }
    public void sendMsg(Event event) {
        try {
            rabbitTemplate.convertAndSend("user-change-queue", event);
            LOG.info("Отправлено событие {} пользователя",event.getOption().equals(Option.DELETED)? "удаления" : event.getOption().equals(Option.CHANGED) ? "изменения": "создания");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public String adminEmails(){
        List<User> admins = userRepository.findByRole(Role.ADMIN);
        return admins.stream()
                .map(User::getEmail)
                .collect(Collectors.joining(","));
    }
}

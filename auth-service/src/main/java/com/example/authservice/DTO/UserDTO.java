package com.example.authservice.DTO;

import com.example.authservice.enums.Role;

public record UserDTO(
        String username,
        String firstName,
        String lastName,
        String email,
        Role role
) {
}

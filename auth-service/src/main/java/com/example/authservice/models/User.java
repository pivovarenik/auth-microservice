package com.example.authservice.models;

import com.example.authservice.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @JsonProperty("id")
    private Long id;

    @JsonProperty("username")
    @Column(name = "username", nullable = false, length = 70)
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @JsonProperty("password")
    @Column(name = "password", nullable = false)
    @NotBlank(message = "Password cannot be null or empty")
    private String password;

    @Column(name = "email", nullable = false, length = 70)
    @JsonProperty("email")
    @NotBlank(message = "Email cannot be null or empty")
    @Email(message = "Email must be valid")
    private String email;

    @JsonProperty("firstName")
    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "First name cannot be null or empty")
    private String firstName;

    @JsonProperty("lastName")
    @Column(name = "last_name", nullable = false)
    @NotBlank(message = "Last name cannot be null or empty")
    private String lastName;

    @Column(name = "role", nullable = false, length = 30)
    @JsonProperty("role")
    @Enumerated(EnumType.STRING)
    @NotNull(message= "Role cannot be null or empty")
    private Role role;
}
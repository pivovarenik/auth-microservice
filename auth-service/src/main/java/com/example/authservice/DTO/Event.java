package com.example.authservice.DTO;

import com.example.authservice.enums.Option;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class Event implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Option option;
    private String username;
    private String email;
    private String addressees;
}
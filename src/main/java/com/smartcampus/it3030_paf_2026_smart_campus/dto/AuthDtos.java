package com.smartcampus.it3030_paf_2026_smart_campus.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {

    public record SignUpRequest(
            @NotBlank(message = "Full name is required")
            String fullName,
            @Email(message = "Invalid email")
            @NotBlank(message = "Email is required")
            String email,
            @Size(min = 6, message = "Password must be at least 6 characters")
            String password
    ) {
    }

    public record SignInRequest(
            @Email(message = "Invalid email")
            @NotBlank(message = "Email is required")
            String email,
            @NotBlank(message = "Password is required")
            String password
    ) {
    }

    public record AuthResponse(
            String message,
            Long userId,
            String fullName,
            String email
    ) {
    }
}

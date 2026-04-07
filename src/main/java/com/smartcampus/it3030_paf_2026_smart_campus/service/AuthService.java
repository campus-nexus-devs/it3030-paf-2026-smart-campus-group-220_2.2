package com.smartcampus.it3030_paf_2026_smart_campus.service;

import com.smartcampus.it3030_paf_2026_smart_campus.entity.AppUser;
import com.smartcampus.it3030_paf_2026_smart_campus.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.smartcampus.it3030_paf_2026_smart_campus.dto.AuthDtos.AuthResponse;
import static com.smartcampus.it3030_paf_2026_smart_campus.dto.AuthDtos.SignInRequest;
import static com.smartcampus.it3030_paf_2026_smart_campus.dto.AuthDtos.SignUpRequest;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse signUp(SignUpRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        if (appUserRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email already registered");
        }

        AppUser user = new AppUser();
        String trimmedName = request.fullName().trim();
        user.setFullName(trimmedName);
        String[] nameParts = trimmedName.split("\\s+", 2);
        user.setFirstName(nameParts[0]);
        user.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        AppUser saved = appUserRepository.save(user);

        return new AuthResponse("Sign up successful", saved.getId(), saved.getFullName(), saved.getEmail());
    }

    public AuthResponse signIn(SignInRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        AppUser user = appUserRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return new AuthResponse("Sign in successful", user.getId(), user.getFullName(), user.getEmail());
    }
}

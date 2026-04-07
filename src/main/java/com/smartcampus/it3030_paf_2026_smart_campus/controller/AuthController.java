package com.smartcampus.it3030_paf_2026_smart_campus.controller;

import com.smartcampus.it3030_paf_2026_smart_campus.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.smartcampus.it3030_paf_2026_smart_campus.dto.AuthDtos.AuthResponse;
import static com.smartcampus.it3030_paf_2026_smart_campus.dto.AuthDtos.SignInRequest;
import static com.smartcampus.it3030_paf_2026_smart_campus.dto.AuthDtos.SignUpRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse signUp(@Valid @RequestBody SignUpRequest request) {
        return authService.signUp(request);
    }

    @PostMapping("/signin")
    public AuthResponse signIn(@Valid @RequestBody SignInRequest request) {
        return authService.signIn(request);
    }
}
